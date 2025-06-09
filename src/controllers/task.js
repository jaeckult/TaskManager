const express = require('express');
const taskRouter = express.Router();
const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();
const { identifyUser } = require('../utils/middleware');
const logger = require('../utils/logger');

// Get all tasks for the user
taskRouter.get('/', identifyUser, async (req, res) => {
    try {
        const tasks = await prisma.task.findMany({
            where: { userId: req.user.id },
            orderBy: { createdAt: 'desc' }
        });
        res.json(tasks);
    } catch (error) {
        logger.error('Error retrieving tasks:', error);
        res.status(500).json({ error: 'Could not retrieve tasks' });
    }
});

// Get task statistics
taskRouter.get('/stats', identifyUser, async (req, res) => {
    try {
        const [totalTasks, completedTasksQuantity, cancelledTasksQuantity, expiredTasksQuantity, TodoQuantity] = await Promise.all([
            prisma.task.count({ where: { userId: req.user.id } }),
            prisma.task.count({ where: { userId: req.user.id, status: 'COMPLETED' } }),
            prisma.task.count({ where: { userId: req.user.id, status: 'CANCELLED' } }),
            prisma.task.count({ where: { userId: req.user.id, status: 'EXPIRED' } }),
            prisma.task.count({ where: { userId: req.user.id, status: 'TODO' } }),
        ]);

        res.json({
            totalTasks,
            completedTasksQuantity,
            cancelledTasksQuantity,
            expiredTasksQuantity,
            TodoQuantity,
        });
    } catch (error) {
        logger.error('Error retrieving task statistics:', error);
        res.status(500).json({ error: 'Could not retrieve task statistics' });
    }
});

// Get a single task by ID
taskRouter.get('/:id', identifyUser, async (req, res) => {
    try {
        const task = await prisma.task.findUnique({
            where: { 
                id: parseInt(req.params.id),
                userId: req.user.id 
            },
        });
        
        if (!task) {
            return res.status(404).json({ error: 'Task not found' });
        }
        
        res.json(task);
    } catch (error) {
        logger.error('Error retrieving task:', error);
        res.status(500).json({ error: 'Could not retrieve task' });
    }
});

// Create a new task
taskRouter.post('/', identifyUser, async (req, res) => {
    const { title, description, startDate, endDate } = req.body;
    const userId = req.user.id;

    // Validate required fields
    if (!title || !description || !startDate || !endDate) {
        return res.status(400).json({ error: 'All fields are required' });
    }

    try {
        const newTask = await prisma.task.create({
            data: {
                title,
                description,
                userId,
                startDate: new Date(startDate),
                endDate: new Date(endDate),
                status: 'TODO', // Default status for new tasks
                createdAt: new Date(),
                updatedAt: new Date()
            },
        });
        res.status(201).json(newTask);
    } catch (error) {
        logger.error('Error creating task:', error);
        res.status(500).json({ error: 'Could not create task' });
    }
});

// Update a task
taskRouter.patch('/:id', identifyUser, async (req, res) => {
    const { title, description, status, startDate, endDate } = req.body;
    const taskId = parseInt(req.params.id);

    // Validate required fields
    if (!title || !description || !startDate || !endDate || !status) {
        return res.status(400).json({ error: 'All fields are required' });
    }

    // Validate status transitions
    try {
        const currentTask = await prisma.task.findUnique({
            where: { 
                id: taskId,
                userId: req.user.id 
            },
        });

        if (!currentTask) {
            return res.status(404).json({ error: 'Task not found' });
        }

        // Validate status transitions
        const validTransitions = {
            'TODO': ['IN_PROGRESS', 'COMPLETED', 'EXPIRED', 'CANCELLED'],
            'IN_PROGRESS': ['COMPLETED', 'EXPIRED', 'CANCELLED'],
            'COMPLETED': [], // Can't transition from completed
            'EXPIRED': [], // Can't transition from expired
            'CANCELLED': [] // Can't transition from cancelled
        };

        if (currentTask.status !== status && 
            !validTransitions[currentTask.status]?.includes(status)) {
            return res.status(400).json({ 
                error: `Cannot change status from ${currentTask.status} to ${status}` 
            });
        }

        const updatedTask = await prisma.task.update({
            where: { 
                id: taskId,
                userId: req.user.id 
            },
            data: {
                title,
                description,
                status,
                startDate: new Date(startDate),
                endDate: new Date(endDate),
                updatedAt: new Date()
            },
        });
        res.json(updatedTask);
    } catch (error) {
        logger.error('Error updating task:', error);
        res.status(500).json({ error: 'Could not update task' });
    }
});

// Delete a task
taskRouter.delete('/:id', identifyUser, async (req, res) => {
    const taskId = parseInt(req.params.id);

    try {
        const task = await prisma.task.findUnique({
            where: { 
                id: taskId,
                userId: req.user.id 
            },
        });

        if (!task) {
            return res.status(404).json({ error: 'Task not found' });
        }

        // Only allow deletion of completed, expired, or cancelled tasks
        if (!['COMPLETED', 'EXPIRED', 'CANCELLED'].includes(task.status)) {
            return res.status(400).json({ 
                error: 'Can only delete completed, expired, or cancelled tasks' 
            });
        }

        await prisma.task.delete({
            where: { 
                id: taskId,
                userId: req.user.id 
            },
        });
        res.status(204).end();
    } catch (error) {
        logger.error('Error deleting task:', error);
        res.status(500).json({ error: 'Could not delete task' });
    }
});

module.exports = taskRouter;