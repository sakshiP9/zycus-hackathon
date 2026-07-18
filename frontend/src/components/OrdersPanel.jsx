import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CircularProgress from '@mui/material/CircularProgress';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import StatusChip from './StatusChip';

const ORDER_STATUSES = [
  { value: '', label: 'All statuses' },
  { value: 'ASSIGNED', label: 'Assigned' },
  { value: 'REASSIGNMENT_PENDING', label: 'Reassignment pending' },
  { value: 'REASSIGNED', label: 'Reassigned' },
  { value: 'DELIVERED', label: 'Delivered' },
];

function formatTime(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString(undefined, {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function OrdersPanel({ orders, loading, statusFilter, onStatusFilterChange, agents }) {
  const agentMap = Object.fromEntries((agents ?? []).map((a) => [a.id, a.name]));

  return (
    <Card // shared card sx — apply to the top-level <Card> in AgentsPanel, SuggestionsPanel, and OrdersPanel
      sx={{
        borderRadius: 2,
        border: '1px solid',
        borderColor: 'divider',
        boxShadow: 'none',
      }}>
      <CardContent>
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexWrap: 'wrap',
            gap: 2,
            mb: 2,
          }}
        >
          <Typography variant="h6">Orders</Typography>
          <FormControl size="small" sx={{ minWidth: 200 }}>
            <InputLabel id="order-status-filter">Filter by status</InputLabel>
            <Select
              labelId="order-status-filter"
              label="Filter by status"
              value={statusFilter}
              onChange={(e) => onStatusFilterChange(e.target.value)}
            >
              {ORDER_STATUSES.map(({ value, label }) => (
                <MenuItem key={value || 'all'} value={value}>
                  {label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>

        {loading && !orders?.length ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress size={32} />
          </Box>
        ) : (
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Order</TableCell>
                  <TableCell>Description</TableCell>
                  <TableCell>Assigned agent</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Created</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {(orders ?? []).map((order) => (
                  <TableRow
                    key={order.id}
                    hover
                    sx={
                      order.status === 'REASSIGNMENT_PENDING'
                        ? { bgcolor: 'action.selected' }
                        : undefined
                    }
                  >
                    <TableCell>
                      <Typography variant="body2" fontWeight={600}>
                        {order.id}
                      </Typography>
                    </TableCell>
                    <TableCell>{order.description}</TableCell>
                    <TableCell>
                      {agentMap[order.assignedAgentId] ?? order.assignedAgentId ?? '—'}
                    </TableCell>
                    <TableCell>
                      <StatusChip status={order.status} />
                    </TableCell>
                    <TableCell>{formatTime(order.createdAt)}</TableCell>
                  </TableRow>
                ))}
                {!orders?.length && (
                  <TableRow>
                    <TableCell colSpan={5} align="center">
                      <Typography variant="body2" color="text.secondary" sx={{ py: 2 }}>
                        No orders found
                      </Typography>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </CardContent>
    </Card>
  );
}

export default OrdersPanel;
