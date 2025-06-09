const express = require('express');
const taskRouter = express.Router();
const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();
const { identifyUser } = require('../utils/middleware');

taskRouter.get('/', async (req, res) => {
    try {
        const tasks = await prisma.task.findMany();
        res.json(tasks);
    } catch (error) {
        res.status(500).json({ error: 'Could not retrieve tasks' });
    }
});

taskRouter.get('/:id', async (req, res) => {
    try {
        const task = await prisma.task.findUnique({
            where: { id: parseInt(req.params.id) },
        });
        if (task) {
            res.json(task);
        } else {
            res.status(404).json({ error: 'Task not found' });
        }
    }
    catch (error) {
        res.status(500).json({ error: 'Could not retrieve task' });
    }
});

taskRouter.post('/', async (req, res) => {
    
    const { title, description, userId } = req.body;

    if (!title || !description || !userId) {
        return res.status(400).json({ error: 'All fields are required' });
    }

    try {
        const newTask = await prisma.task.create({
            data: {
                title,
                description,
                userId,
            },
        });
        res.status(201).json(newTask);
    } catch (error) {
        res.status(500).json({ error: 'Could not create task' });
    }
});

taskRouter.patch('/:id', async (req, res) => {
    const { title, description, status } = req.body;

    if (!title && !description && !status) {
        return res.status(400).json({ error: 'At least one field is required' });
    }

    try {
        const updatedTask = await prisma.task.update({
            where: { id: parseInt(req.params.id) },
            data: {
                title,
                description,
                status,
            },
        });
        res.json(updatedTask);
    } catch (error) {
        res.status(500).json({ error: 'Could not update task' });
    }
});
taskRouter.delete('/:id', async (req, res) => {
    try {
        await prisma.task.delete({
            where: { id: parseInt(req.params.id) },
        });
        res.status(204).end();
    } catch (error) {
        res.status(500).json({ error: 'Could not delete task' });
    }
});