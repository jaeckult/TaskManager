const express = require('express');
const taskRouter = express.Router();
const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();
const { identifyUser } = require('../utils/middleware');
const logger = require('../utils/logger');

taskRouter.get('/', identifyUser, async (req, res) => {
    try {
        const tasks = await prisma.task.findMany({
            where: { userId: req.user.id },
        });
        res.json(tasks);
    } catch (error) {
        res.status(500).json({ error: 'Could not retrieve tasks' });
    }
});

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

taskRouter.get('/:id', identifyUser, async (req, res) => {
    try {
        const task = await prisma.task.findUnique({
            where: { id: parseInt(req.params.id), userId: req.user.id },
        });
        if (task) {
            res.json(task);
        } else {
            res.status(404).json({ error: 'Task not found' });
        }
    } catch (error) {
        res.status(500).json({ error: 'Could not retrieve task' });
    }
});

taskRouter.post('/', identifyUser, async (req, res) => {
    console.log(req.body);
    
    const { title, description, startDate, endDate } = req.body;
    const userId = req.user.id; // Using the authenticated user's ID

    if (!title || !description || !userId || !startDate || !endDate) {
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
            },
        });
        res.status(201).json(newTask);
    } catch (error) {
        logger.error('Error creating task:', error);
        console.error('Error creating task:', error);
        res.status(500).json({ error: 'Could not create task' });
    }
});

taskRouter.patch('/:id', identifyUser, async (req, res) => {
    const { title, description, status, startDate, endDate } = req.body;

    if (!title && !description && !status) {
        return res.status(400).json({ error: 'At least one field is required' });
    }

    try {
        const updatedTask = await prisma.task.update({
            where: { id: parseInt(req.params.id), userId: req.user.id },
            data: {
                title,
                description,
                status,
                startDate,
                endDate,
            },
        });
        res.json(updatedTask);
    } catch (error) {
        res.status(500).json({ error: 'Could not update task' });
    }
});

taskRouter.delete('/:id', identifyUser, async (req, res) => {
    try {
        await prisma.task.delete({
            where: { id: parseInt(req.params.id), userId: req.user.id },
        });
        res.status(204).end();
    } catch (error) {
        res.status(500).json({ error: 'Could not delete task' });
    }
});

module.exports = taskRouter;