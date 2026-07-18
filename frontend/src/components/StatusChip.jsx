import Chip from '@mui/material/Chip';

const STATUS_COLORS = {
  AVAILABLE: 'success',
  BUSY: 'warning',
  OFFLINE: 'default',
  ASSIGNED: 'info',
  REASSIGNMENT_PENDING: 'warning',
  REASSIGNED: 'secondary',
  DELIVERED: 'success',
  PENDING: 'warning',
  ACCEPTED: 'success',
  REJECTED: 'error',
};

function StatusChip({ status, size = 'small' }) {
  const color = STATUS_COLORS[status] ?? 'default';
  const label = status?.replace(/_/g, ' ') ?? 'UNKNOWN';

  return (
    <Chip
      label={label}
      color={color}
      size={size}
      variant={color === 'default' ? 'outlined' : 'filled'}
    />
  );
}

export default StatusChip;
