const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

async function main() {
  // Make sure there’s a user to assign tasks to
  const user = await prisma.user.findFirst();

  if (!user) {
    console.error('No user found. Seed a user before seeding tasks.');
    return;
  }

  const tasks = [
    {
      title: 'Design Landing Page',
      description: 'Create the UI mockup for the new landing page.',
      status: 'TODO',
      startDate: new Date('2025-06-01'),
      endDate: new Date('2025-06-05'),
      userId: user.id,
    },
    {
      title: 'Implement Auth',
      description: 'Add login and registration functionality.',
      status: 'COMPLETED',
      startDate: new Date('2025-05-20'),
      endDate: new Date('2025-05-25'),
      userId: user.id,
    },
    {
      title: 'Database Backup',
      description: 'Backup the production database weekly.',
      status: 'CANCELLED',
      startDate: new Date('2025-06-01'),
      endDate: new Date('2025-06-02'),
      userId: user.id,
    },
    {
      title: 'Client Feedback Review',
      description: 'Review feedback from the last deployment.',
      status: 'EXPIRED',
      startDate: new Date('2025-05-10'),
      endDate: new Date('2025-05-15'),
      userId: user.id,
    },
  ];

  await prisma.task.createMany({ data: tasks });
  console.log('Seeded tasks successfully.');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
