const express = require('express');
const app = express();
const cors = require('cors');
const userRouter = require('./controllers/user');
const loginRouter = require('./controllers/login');
const signupRouter = require('./controllers/signup');
const taskRouter = require('./controllers/task');
const { getTokenFrom } = require('./utils/middleware');


app.use(express.json());
app.use(cors())
app.use(getTokenFrom);

app.use('/api/users', userRouter);
app.use('/api/login', loginRouter);
app.use('/api/signup', signupRouter);
app.use('/api/tasks', taskRouter);

app.get('/', (req, res)=>{
    res.send('<h1>Welcome to the API</h1>')
});

module.exports = app;