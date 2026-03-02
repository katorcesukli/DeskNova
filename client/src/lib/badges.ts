export function getStatusClasses(status: string) {
  switch (status) {
    case 'OPEN':
      return 'bg-green-100 text-green-800';
    case 'IN_PROGRESS':
      return 'bg-yellow-100 text-yellow-800';
    case 'RESOLVED':
      return 'bg-blue-100 text-blue-800';
    case 'CLOSED':
      return 'bg-gray-100 text-gray-800';
    default:
      return 'bg-gray-50 text-gray-700';
  }
}

export function getPriorityClasses(priority: string) {
  switch (priority) {
    case 'LOW':
      return 'bg-green-50 text-green-800';
    case 'MEDIUM':
      return 'bg-yellow-50 text-yellow-800';
    case 'HIGH':
      return 'bg-red-50 text-red-800';
    default:
      return 'bg-gray-50 text-gray-700';
  }
}
