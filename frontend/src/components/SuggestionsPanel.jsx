// SuggestionsPanel.jsx
import { useState } from 'react';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import CircularProgress from '@mui/material/CircularProgress';
import LinearProgress from '@mui/material/LinearProgress';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { updateSuggestionStatus } from '../api/suggestions';

function formatConfidence(value) {
  return `${Math.round((value ?? 0) * 100)}%`;
}

function confidenceColor(value) {
  const pct = (value ?? 0) * 100;
  if (pct > 80) return 'success';
  if (pct >= 50) return 'warning';
  return 'error';
}

function formatTime(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString(undefined, {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function SuggestionCard({ suggestion, agentName, onAction, acting, error }) {
  const isOfflineTrigger = suggestion.triggerReason === 'AGENT_OFFLINE';
  const isActingOnThis = acting?.id === suggestion.id;
  const isAcceptLoading = isActingOnThis && acting.status === 'ACCEPTED';
  const isRejectLoading = isActingOnThis && acting.status === 'REJECTED';

  return (
    <Card
      sx={{
        border: '1px solid',
        borderColor: isOfflineTrigger ? 'warning.main' : 'divider',
        borderLeftWidth: isOfflineTrigger ? 4 : 1,
        bgcolor: isOfflineTrigger ? 'action.selected' : 'background.paper',
        borderRadius: 2,
        boxShadow: 'none',
      }}
    >
      <CardContent>
        <Stack spacing={1.5}>
          <Stack direction="row" justifyContent="space-between" alignItems="flex-start" gap={1}>
            <Box>
              <Stack direction="row" alignItems="center" gap={1} flexWrap="wrap">
                <Typography variant="subtitle1" fontWeight={700}>
                  Order {suggestion.orderId}
                </Typography>
                {isOfflineTrigger ? (
                  <Chip label="🔄 Auto Re-plan" color="warning" size="small" variant="filled" />
                ) : (
                  <Chip label="Manual Request" size="small" variant="outlined" />
                )}
              </Stack>
              <Typography variant="caption" color="text.secondary">
                Suggestion #{suggestion.id} · {formatTime(suggestion.createdAt)}
              </Typography>
            </Box>
            <Chip
              label={formatConfidence(suggestion.confidence)}
              color={confidenceColor(suggestion.confidence)}
              size="small"
            />
          </Stack>

          <Box>
            <Typography variant="body2" color="text.secondary">
              Recommended agent
            </Typography>
            <Typography variant="body1" fontWeight={600}>
              {agentName ?? suggestion.recommendedAgentId}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              {suggestion.recommendedAgentId}
            </Typography>
          </Box>

          <Box
            sx={{
              p: 1.5,
              borderRadius: 1,
              bgcolor: 'action.hover',
              border: '1px solid',
              borderColor: 'divider',
            }}
          >
            <Typography variant="overline" color="text.secondary" sx={{ display: 'block', mb: 0.5 }}>
              AI reasoning
            </Typography>
            <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
              {suggestion.reasoning}
            </Typography>
          </Box>

          {error && (
            <Alert severity="error" sx={{ py: 0 }}>
              {error}
            </Alert>
          )}

          <Stack direction="row" spacing={1} justifyContent="flex-end">
            <Button
              variant="outlined"
              color="error"
              size="small"
              disabled={isActingOnThis}
              onClick={() => onAction(suggestion.id, 'REJECTED')}
            >
              {isRejectLoading ? <CircularProgress size={18} /> : 'Reject'}
            </Button>
            <Button
              variant="contained"
              color="success"
              size="small"
              disabled={isActingOnThis}
              onClick={() => onAction(suggestion.id, 'ACCEPTED')}
            >
              {isAcceptLoading ? <CircularProgress size={18} color="inherit" /> : 'Accept'}
            </Button>
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}

function SuggestionsPanel({ suggestions, agents, loading, onResolved }) {
  const [acting, setActing] = useState(null); // { id, status } | null
  const [errors, setErrors] = useState({});

  const agentMap = Object.fromEntries((agents ?? []).map((a) => [a.id, a.name]));
  const offlineTriggered = (suggestions ?? []).filter(
    (s) => s.triggerReason === 'AGENT_OFFLINE',
  ).length;

  async function handleAction(id, status) {
    setActing({ id, status });
    setErrors((prev) => ({ ...prev, [id]: null }));
    try {
      await updateSuggestionStatus(id, status);
      onResolved(id);
    } catch (err) {
      const message = err.response?.data?.message ?? err.message ?? 'Failed to update suggestion';
      setErrors((prev) => ({ ...prev, [id]: message }));
    } finally {
      setActing(null);
    }
  }

  return (
    <Card sx={{ borderRadius: 2, border: '1px solid', borderColor: 'divider', boxShadow: 'none' }}>
      <CardContent>
        <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 1 }}>
          <Typography variant="h6">Pending reassignments</Typography>
          {(suggestions ?? []).length > 0 && (
            <Chip label={`${(suggestions ?? []).length} pending`} color="warning" size="small" />
          )}
        </Stack>

        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Review AI recommendations and accept or reject. Suggestions triggered by an agent going
          offline are highlighted.
          {offlineTriggered > 0 && (
            <>
              {' '}
              <strong>{offlineTriggered}</strong> from offline events.
            </>
          )}
        </Typography>

        {loading && !suggestions?.length ? (
          <Box sx={{ py: 4 }}>
            <LinearProgress />
          </Box>
        ) : (suggestions ?? []).length === 0 ? (
          <Box
            sx={{
              py: 6,
              px: 2,
              textAlign: 'center',
              borderRadius: 2,
              border: '1px dashed',
              borderColor: 'divider',
            }}
          >
            <Typography variant="body1" color="text.secondary" gutterBottom>
              No pending suggestions — all caught up
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Set an agent offline to generate automatic reassignment recommendations.
            </Typography>
          </Box>
        ) : (
          <Box sx={{ maxHeight: 640, overflowY: 'auto', pr: 0.5 }}>
            <Stack spacing={2}>
              {(suggestions ?? []).map((suggestion) => (
                <SuggestionCard
                  key={suggestion.id}
                  suggestion={suggestion}
                  agentName={agentMap[suggestion.recommendedAgentId]}
                  onAction={handleAction}
                  acting={acting}
                  error={errors[suggestion.id]}
                />
              ))}
            </Stack>
          </Box>
        )}
      </CardContent>
    </Card>
  );
}

export default SuggestionsPanel;