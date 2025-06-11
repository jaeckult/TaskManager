const express = require('express');
const projectRouter = express.Router();
const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();
const { identifyUser } = require('../utils/middleware');

// Get all projects for a user
projectRouter.use(identifyUser);
projectRouter.get('/', async (req, res) => {
    console.log('Fetching all projects for user:', req.user.username);
    try {
        const userId = req.user.id;
        const projects = await prisma.project.findMany({
            where: {
                OR: [
                    { ownerId: userId },
                    { sharedWith: { some: { userId: userId } } }
                ]
            },
            include: {
                tasks: {
                    include: {
                        user: {
                            select: { username: true }
                        }
                    }
                },
                owner: {
                    select: { username: true }
                },
                sharedWith: {
                    include: {
                        user: {
                            select: { username: true }
                        }
                    }
                }
            }
        });
        res.json(projects);
    } catch (error) {
        console.error('Error fetching projects:', error);
        res.status(500).json({ error: 'Could not retrieve projects' });
    }
});

// Get a specific project
projectRouter.get('/:id', async (req, res) => {
    try {
        const userId = req.user.id;
        const projectId = parseInt(req.params.id);

        const project = await prisma.project.findFirst({
            where: {
                id: projectId,
                OR: [
                    { ownerId: userId },
                    { sharedWith: { some: { userId: userId } } }
                ]
            },
            include: {
                tasks: {
                    include: {
                        user: {
                            select: { username: true }
                        }
                    }
                },
                owner: {
                    select: { username: true }
                },
                sharedWith: {
                    include: {
                        user: {
                            select: { username: true }
                        }
                    }
                }
            }
        });

        if (!project) {
            return res.status(404).json({ error: 'Project not found or access denied' });
        }

        res.json(project);
    } catch (error) {
        console.error('Error fetching project:', error);
        res.status(500).json({ error: 'Could not retrieve project' });
    }
});

// Create a new project
projectRouter.post('/', async (req, res) => {
    const { title, description } = req.body;
    const userId = req.user.id;

    if (!title) {
        return res.status(400).json({ error: 'Title is required' });
    }

    try {
        const newProject = await prisma.project.create({
            data: {
                title,
                description,
                ownerId: userId,
            },
            include: {
                tasks: true,
                owner: {
                    select: { username: true }
                }
            }
        });
        res.status(201).json(newProject);
    } catch (error) {
        console.error('Error creating project:', error);
        res.status(500).json({ error: 'Could not create project' });
    }
});

// Update a project
projectRouter.patch('/:id', async (req, res) => {
    const { title, description } = req.body;
    const projectId = parseInt(req.params.id);
    const userId = req.user.id;

    try {
        // Check if user is owner or has access
        const project = await prisma.project.findFirst({
            where: {
                id: projectId,
                OR: [
                    { ownerId: userId },
                    { sharedWith: { some: { userId: userId } } }
                ]
            }
        });

        if (!project) {
            return res.status(403).json({ error: 'Not authorized to update this project' });
        }

        const updatedProject = await prisma.project.update({
            where: { id: projectId },
            data: {
                title,
                description,
            },
            include: {
                tasks: {
                    include: {
                        user: {
                            select: { username: true }
                        }
                    }
                },
                owner: {
                    select: { username: true }
                },
                sharedWith: {
                    include: {
                        user: {
                            select: { username: true }
                        }
                    }
                }
            }
        });
        res.json(updatedProject);
    } catch (error) {
        console.error('Error updating project:', error);
        res.status(500).json({ error: 'Could not update project' });
    }
});

// Delete a project
projectRouter.delete('/:id', async (req, res) => {
    const projectId = parseInt(req.params.id);
    const userId = req.user.id;

    try {
        // Check if user is owner
        const project = await prisma.project.findFirst({
            where: {
                id: projectId,
                ownerId: userId
            }
        });

        if (!project) {
            return res.status(403).json({ error: 'Not authorized to delete this project' });
        }

        // Delete all related records first
        await prisma.$transaction([
            prisma.projectSharedUser.deleteMany({
                where: { projectId }
            }),
            prisma.projectShareRequest.deleteMany({
                where: { projectId }
            }),
            prisma.task.updateMany({
                where: { projectId },
                data: { projectId: null }
            }),
            prisma.project.delete({
                where: { id: projectId }
            })
        ]);

        res.status(204).end();
    } catch (error) {
        console.error('Error deleting project:', error);
        res.status(500).json({ error: 'Could not delete project' });
    }
});

// Share project with another user
projectRouter.post('/:id/share', async (req, res) => {
    const projectId = parseInt(req.params.id);
    const { targetUserId } = req.body;
    const userId = req.user.id;

    try {
        // Check if user is owner
        const project = await prisma.project.findFirst({
            where: {
                id: projectId,
                ownerId: userId
            }
        });

        if (!project) {
            return res.status(403).json({ error: 'Not authorized to share this project' });
        }

        // Check if target user exists
        const targetUser = await prisma.user.findUnique({
            where: { id: targetUserId }
        });

        if (!targetUser) {
            return res.status(404).json({ error: 'Target user not found' });
        }

        // Check if project is already shared with the user
        const existingShare = await prisma.projectSharedUser.findUnique({
            where: {
                projectId_userId: {
                    projectId,
                    userId: targetUserId
                }
            }
        });

        if (existingShare) {
            return res.status(400).json({ error: 'Project is already shared with this user' });
        }

        // Check for existing pending request
        const existingRequest = await prisma.projectShareRequest.findFirst({
            where: {
                projectId,
                toUserId: targetUserId,
                status: 'PENDING'
            }
        });

        if (existingRequest) {
            return res.status(400).json({ error: 'A pending share request already exists' });
        }

        // Create sharing request
        const shareRequest = await prisma.projectShareRequest.create({
            data: {
                projectId,
                fromUserId: userId,
                toUserId: targetUserId,
                status: 'PENDING'
            },
            include: {
                project: true,
                fromUser: {
                    select: { username: true }
                },
                toUser: {
                    select: { username: true }
                }
            }
        });

        res.status(201).json(shareRequest);
    } catch (error) {
        console.error('Error creating share request:', error);
        res.status(500).json({ error: 'Could not create share request' });
    }
});

// Handle project share request (accept/decline)
projectRouter.patch('/share/:requestId', async (req, res) => {
    const { requestId } = req.params;
    const { status } = req.body; // 'ACCEPTED' or 'DECLINED'
    const userId = req.user.id;

    if (!['ACCEPTED', 'DECLINED'].includes(status)) {
        return res.status(400).json({ error: 'Invalid status. Must be ACCEPTED or DECLINED' });
    }

    try {
        const shareRequest = await prisma.projectShareRequest.findUnique({
            where: { id: parseInt(requestId) },
            include: { project: true }
        });

        if (!shareRequest || shareRequest.toUserId !== userId) {
            return res.status(403).json({ error: 'Not authorized to handle this request' });
        }

        if (shareRequest.status !== 'PENDING') {
            return res.status(400).json({ error: 'This request has already been handled' });
        }

        if (status === 'ACCEPTED') {
            // Add user to project's shared users
            await prisma.projectSharedUser.create({
                data: {
                    projectId: shareRequest.projectId,
                    userId: userId
                }
            });
        }

        // Update request status
        const updatedRequest = await prisma.projectShareRequest.update({
            where: { id: parseInt(requestId) },
            data: { status },
            include: {
                project: true,
                fromUser: {
                    select: { username: true }
                },
                toUser: {
                    select: { username: true }
                }
            }
        });

        res.json(updatedRequest);
    } catch (error) {
        console.error('Error handling share request:', error);
        res.status(500).json({ error: 'Could not handle share request' });
    }
});

// Get pending share requests for a user
projectRouter.get('/share/requests', async (req, res) => {
    const userId = req.user.id;

    try {
        const requests = await prisma.projectShareRequest.findMany({
            where: {
                toUserId: userId,
                status: 'PENDING'
            },
            include: {
                project: true,
                fromUser: {
                    select: { username: true }
                }
            }
        });
        res.json(requests);
    } catch (error) {
        console.error('Error fetching share requests:', error);
        res.status(500).json({ error: 'Could not retrieve share requests' });
    }
});

// Remove user from shared project
projectRouter.delete('/:id/share/:userId', async (req, res) => {
    const projectId = parseInt(req.params.id);
    const targetUserId = parseInt(req.params.userId);
    const userId = req.user.id;

    try {
        // Check if user is owner
        const project = await prisma.project.findFirst({
            where: {
                id: projectId,
                ownerId: userId
            }
        });

        if (!project) {
            return res.status(403).json({ error: 'Not authorized to remove users from this project' });
        }

        // Remove user from shared users
        await prisma.projectSharedUser.delete({
            where: {
                projectId_userId: {
                    projectId,
                    userId: targetUserId
                }
            }
        });

        res.status(204).end();
    } catch (error) {
        console.error('Error removing user from project:', error);
        res.status(500).json({ error: 'Could not remove user from project' });
    }
});

module.exports = projectRouter;