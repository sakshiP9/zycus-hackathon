// RoutingStrategyToggle.jsx
import Box from '@mui/material/Box';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';

function RoutingStrategyToggle({ strategy, loading, onChange }) {
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
      <Typography
        variant="caption"
        sx={{ color: 'rgba(255,255,255,0.85)', whiteSpace: 'nowrap' }}
      >
        Routing
      </Typography>

      <ToggleButtonGroup
        value={strategy ?? ''}
        exclusive
        size="small"
        disabled={loading}
        onChange={(e, value) => value && onChange(value)}
        sx={{
          bgcolor: 'rgba(255,255,255,0.12)',
          '& .MuiToggleButton-root': {
            color: 'rgba(255,255,255,0.85)',
            border: 'none',
            textTransform: 'none',
            px: 1.5,
            py: 0.25,
            fontSize: '0.8rem',
            '&.Mui-selected': {
              bgcolor: 'rgba(255,255,255,0.9)',
              color: 'primary.main',
              fontWeight: 700,
              '&:hover': {
                bgcolor: 'rgba(255,255,255,0.95)',
              },
            },
            '&:hover': {
              bgcolor: 'rgba(255,255,255,0.2)',
            },
          },
          '&.Mui-selected': {
            bgcolor: '#F2A93B',
            color: '#0F1530',
            fontWeight: 700,
            '&:hover': {
              bgcolor: '#F5B85C',
            },
          },
        }}
      >
        <Tooltip title="AI-powered agent matching">
          <ToggleButton value="ai">AI</ToggleButton>
        </Tooltip>
        <Tooltip title="Rule-based fallback matching">
          <ToggleButton value="ruleBased">Rule-based</ToggleButton>
        </Tooltip>
      </ToggleButtonGroup>

      {loading && <CircularProgress size={16} sx={{ color: 'rgba(255,255,255,0.85)' }} />}
    </Box>
  );
}

export default RoutingStrategyToggle; 