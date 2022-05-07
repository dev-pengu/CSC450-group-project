const isAdmin = (role) => role === 'ADMIN' || role === 'OWNER';

const isAdult = (role) => role === 'ADMIN' || role === 'OWNER' || role === 'ADULT';

export { isAdmin, isAdult };
