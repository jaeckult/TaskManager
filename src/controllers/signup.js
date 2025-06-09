const express = require('express');
const signupRouter = express.Router();
const bcrypt = require('bcrypt');
const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();
const { identifyUser } = require('../utils/middleware');
const { parse } = require('date-fns');

signupRouter.post('/', async (req, res) => {
  console.log('Received signup request:', req.body);
  
  const {
    username,
    password
  } = req.body;
  

  // Validate required fields
  if (!username || !password) {
    return res.status(400).json({ error: 'All fields are required' });
  }
//   if (password.length < 6) {
//     return res.status(400).json({ error: 'Password must be at least 6 characters long' });
//   }

  try {
    // Check for existing user
    const existingUser = await prisma.user.findFirst({
      where: {
        username,
      }
    });

    if (existingUser) {
      return res.status(400).json({
        error: existingUser.username === username ? 'Username already taken' : 'Email already exists'
      });
    }

    const saltRounds = 10;
    const passwordHash = await bcrypt.hash(password, saltRounds);

    // Create user, profile, and initial leave balances in a transaction
    const newUser = await prisma.$transaction(async (prisma) => {
      // Create the user
      const user = await prisma.user.create({
        data: {
          username,
          password: passwordHash,
        }
      });
      console.log('Received signup data:', user);

      return user;
    });

    res.status(201).json({
      username: newUser.username,
    });
  } catch (error) {
    console.error('Error during signup:', error);
    res.status(500).json({ error: 'Internal server error during signup' });
  } finally {
    await prisma.$disconnect();
  }
});

module.exports = signupRouter;