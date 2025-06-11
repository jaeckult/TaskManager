const { PrismaClient, TaskStatus } = require('@prisma/client');
const bcrypt = require('bcrypt');
const prisma = new PrismaClient();

async function main() {
  try {
    // Clear existing data
    await prisma.$transaction([
      prisma.task.deleteMany(),
      prisma.projectSharedUser.deleteMany(),
      prisma.projectShareRequest.deleteMany(),
      prisma.project.deleteMany(),
      prisma.user.deleteMany(),
    ]);

    console.log('Cleared existing data');

    // Create users
    const passwordHash = await bcrypt.hash('password123', 10);
    const users = await Promise.all([
      prisma.user.create({
        data: {
          username: 'john_doe',
          email: 'john@example.com',
          password: passwordHash,
        },
      }),
      prisma.user.create({
        data: {
          username: 'jane_smith',
          email: 'jane@example.com',
          password: passwordHash,
        },
      }),
      prisma.user.create({
        data: {
          username: 'bob_wilson',
          email: 'bob@example.com',
          password: passwordHash,
        },
      }),
    ]);

    console.log('Created users');

    // Create projects
    const projects = await Promise.all([
      prisma.project.create({
        data: {
          title: 'Website Redesign',
          description: 'Complete overhaul of company website',
          ownerId: users[0].id,
        },
      }),
      prisma.project.create({
        data: {
          title: 'Mobile App Development',
          description: 'New mobile app for iOS and Android',
          ownerId: users[1].id,
        },
      }),
      prisma.project.create({
        data: {
          title: 'Database Migration',
          description: 'Migrate to new database system',
          ownerId: users[2].id,
        },
      }),
    ]);

    console.log('Created projects');

    // Create tasks for each project
    const tasks = [];
    for (const project of projects) {
      tasks.push(
        prisma.task.create({
          data: {
            title: `Design ${project.title}`,
            description: `Create initial design for ${project.title}`,
            status: TaskStatus.TODO,
            startDate: new Date('2024-03-01'),
            endDate: new Date('2024-03-15'),
            userId: project.ownerId,
            projectId: project.id,
          },
        }),
        prisma.task.create({
          data: {
            title: `Implement ${project.title}`,
            description: `Start implementation of ${project.title}`,
            status: TaskStatus.IN_PROGRESS,
            startDate: new Date('2024-03-16'),
            endDate: new Date('2024-03-30'),
            userId: project.ownerId,
            projectId: project.id,
          },
        }),
        prisma.task.create({
          data: {
            title: `Test ${project.title}`,
            description: `Testing phase for ${project.title}`,
            status: TaskStatus.COMPLETED,
            startDate: new Date('2024-02-01'),
            endDate: new Date('2024-02-15'),
            userId: project.ownerId,
            projectId: project.id,
          },
        })
      );
    }
    await Promise.all(tasks);

    console.log('Created tasks');

    // Create project sharing relationships
    const shareRelations = await Promise.all([
      // John shares his project with Jane
      prisma.projectSharedUser.create({
        data: {
          projectId: projects[0].id,
          userId: users[1].id,
        },
      }),
      // Jane shares her project with Bob
      prisma.projectSharedUser.create({
        data: {
          projectId: projects[1].id,
          userId: users[2].id,
        },
      }),
    ]);

    console.log('Created project sharing relationships');

    // Create some pending share requests
    const shareRequests = await Promise.all([
      prisma.projectShareRequest.create({
        data: {
          projectId: projects[0].id,
          fromUserId: users[0].id,
          toUserId: users[2].id,
          status: 'PENDING',
        },
      }),
      prisma.projectShareRequest.create({
        data: {
          projectId: projects[2].id,
          fromUserId: users[2].id,
          toUserId: users[0].id,
          status: 'PENDING',
        },
      }),
    ]);

    console.log('Created share requests');

    // Create some individual tasks not associated with projects
    const individualTasks = await Promise.all([
      prisma.task.create({
        data: {
          title: 'Weekly Team Meeting',
          description: 'Regular team sync-up meeting',
          status: TaskStatus.TODO,
          startDate: new Date('2024-03-01'),
          endDate: new Date('2024-03-01'),
          userId: users[0].id,
        },
      }),
      prisma.task.create({
        data: {
          title: 'Code Review',
          description: 'Review pull requests',
          status: TaskStatus.IN_PROGRESS,
          startDate: new Date('2024-03-01'),
          endDate: new Date('2024-03-02'),
          userId: users[1].id,
        },
      }),
    ]);

    console.log('Created individual tasks');

    console.log('Database seeded successfully!');
    console.log('\nTest Users:');
    console.log('1. john_doe (password: password123)');
    console.log('2. jane_smith (password: password123)');
    console.log('3. bob_wilson (password: password123)');

  } catch (error) {
    console.error('Error seeding database:', error);
    throw error;
  }
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
