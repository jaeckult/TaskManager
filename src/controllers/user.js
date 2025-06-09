const express = require('express');
const userRouter = express.Router();
const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();
const bcrypt = require('bcrypt');
const signupRouter = require('./signup');
const { identifyUser } = require('../utils/middleware');


userRouter.get('/', async (req, res) => {
    try {
        const users = await prisma.user.findMany();
        res.json(users);
    } catch (error) {
        res.status(500).json({ error: 'Could not retrieve users' });
    }
});

userRouter.get('/:id', async (req, res) => {
    try {
        const user = await prisma.user.findUnique({
            where: { id: parseInt(req.params.id) },
        });
        if (user) {
            res.json(user);
        } else {
            res.status(404).json({ error: 'User not found' });
        }
    }
    catch (error) {
        res.status(500).json({ error: 'Could not retrieve user' });
    }
});

userRouter.patch('/:id', identifyUser, async (req, res) => {
    console.log(req.body);
    const { username, password } = req.body;

    const updateData = {};

    if (username) {
        updateData.username = username;
    }

    if (password) {
        try {
            const saltRounds = 10;
            const hashedPassword = await bcrypt.hash(password, saltRounds);
            updateData.password = hashedPassword;
        } catch (err) {
            return res.status(500).json({ error: 'Error hashing password' });
        }
    }
    console.log("am here");
    

    // if (Object.keys(updateData).length === 0) {
    //     return res.status(400).json({ error: 'No valid fields to update' });
    // }
    console.log("am here");

    try {
        const updatedUser = await prisma.user.update({
            where: { id: parseInt(req.params.id) },
            data: updateData,
        });
        res.json(updatedUser);
    } catch (error) {
        res.status(500).json({ error: 'Could not update user' });
    }
});

module.exports = userRouter;