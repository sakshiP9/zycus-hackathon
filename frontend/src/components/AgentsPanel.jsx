// AgentsPanel.jsx
import { useState } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CircularProgress from '@mui/material/CircularProgress';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import { updateAgentStatus } from '../api/agents';
import StatusChip from './StatusChip';

function AgentsPanel({ agents, loading, onRefresh }) {
  const [updatingId, setUpdatingId] = useState(null);
  const [errors, setErrors] = useState({});

  async function handleToggleStatus(agent) {
    const nextStatus = agent.status === 'OFFLINE' ? 'AVAILABLE' : 'OFFLINE';
    setUpdatingId(agent.id);
    setErrors((prev) => ({ ...prev, [agent.id]: null }));
    try {
      await updateAgentStatus(agent.id, nextStatus);
      onRefresh();
    } catch (err) {
      const message = err.response?.data?.message ?? err.message ?? 'Failed to update agent';
      setErrors((prev) => ({ ...prev, [agent.id]: message }));
    } finally {
      setUpdatingId(null);
    }
  }

  return (
    <Card
      // shared card sx — apply to the top-level <Card> in AgentsPanel, SuggestionsPanel, and OrdersPanel
      sx={{
        borderRadius: 2,
        border: '1px solid',
        borderColor: 'divider',
        boxShadow: 'none',
      }}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Delivery agents
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Mark an agent offline to trigger automatic reassignment suggestions.
        </Typography>

        {loading && !agents?.length ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress size={32} />
          </Box>
        ) : (
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Agent</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell align="center">Active orders</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {(agents ?? []).map((agent) => {
                  const isUpdating = updatingId === agent.id;
                  const isOffline = agent.status === 'OFFLINE';
                  const rowError = errors[agent.id];
                  return (
                    <TableRow key={agent.id} hover>
                      <TableCell>
                        <Typography variant="body2" fontWeight={600}>
                          {agent.name}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {agent.id}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <StatusChip status={agent.status} />
                      </TableCell>
                      <TableCell align="center">{agent.activeOrderCount}</TableCell>
                      <TableCell align="right">
                        <Stack spacing={0.5} alignItems="flex-end">
                          <Button
                            size="small"
                            color={isOffline ? 'success' : 'error'}
                            variant="outlined"
                            disabled={isUpdating}
                            onClick={() => handleToggleStatus(agent)}
                            sx={{ minWidth: 120 }}
                          >
                            {isUpdating ? (
                              <CircularProgress size={16} />
                            ) : isOffline ? (
                              'Mark available'
                            ) : (
                              'Mark offline'
                            )}
                          </Button>
                          {rowError && (
                            <Typography variant="caption" color="error">
                              {rowError}
                            </Typography>
                          )}
                        </Stack>
                      </TableCell>
                    </TableRow>
                  );
                })}
                {!agents?.length && (
                  <TableRow>
                    <TableCell colSpan={4} align="center">
                      <Typography variant="body2" color="text.secondary" sx={{ py: 2 }}>
                        No agents found
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

export default AgentsPanel;