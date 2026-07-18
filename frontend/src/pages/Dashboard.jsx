// Dashboard.jsx
import { useCallback, useState } from 'react';
import Alert from '@mui/material/Alert';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Snackbar from '@mui/material/Snackbar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { fetchAgents } from '../api/agents';
import { fetchRoutingStrategy, updateRoutingStrategy } from '../api/config';
import { fetchOrders } from '../api/orders';
import { fetchSuggestions } from '../api/suggestions';
import AgentsPanel from '../components/AgentsPanel';
import OrdersPanel from '../components/OrdersPanel';
import RoutingStrategyToggle from '../components/RoutingStrategyToggle';
import SuggestionsPanel from '../components/SuggestionsPanel';
import { usePolling } from '../hooks/usePolling';
import RefreshIcon from '@mui/icons-material/Refresh';

function Dashboard() {
  const [agents, setAgents] = useState([]);
  const [orders, setOrders] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [routingStrategy, setRoutingStrategy] = useState(null);
  const [orderStatusFilter, setOrderStatusFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [strategyLoading, setStrategyLoading] = useState(false);
  const [lastUpdated, setLastUpdated] = useState(null);
  const [error, setError] = useState(null);
  const [toast, setToast] = useState(null);

  const loadData = useCallback(async (showSpinner = false) => {
    if (showSpinner) setLoading(true);
    setError(null);
    try {
      const [agentsData, ordersData, suggestionsData, strategyData] = await Promise.all([
        fetchAgents(),
        fetchOrders(orderStatusFilter || undefined),
        fetchSuggestions(),
        fetchRoutingStrategy(),
      ]);
      setAgents(agentsData);
      setOrders(ordersData);
      setSuggestions(suggestionsData);
      setRoutingStrategy(strategyData.active);
      setLastUpdated(new Date());
    } catch (err) {
      setError(err.response?.data?.message ?? err.message ?? 'Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  }, [orderStatusFilter]);

  usePolling(() => loadData(false), 5000, [orderStatusFilter]);

  const handleRefresh = () => loadData(true);

  const handleSuggestionResolved = (id) => {
    setSuggestions((prev) => prev.filter((s) => s.id !== id));
  };

  const handleStrategyChange = async (strategy) => {
    setStrategyLoading(true);
    try {
      const result = await updateRoutingStrategy(strategy);
      setRoutingStrategy(result.active);
      setToast(`Routing strategy switched to ${result.active === 'ai' ? 'AI' : 'rule-based'}`);
    } catch (err) {
      setError(err.response?.data?.message ?? err.message ?? 'Failed to update routing strategy');
    } finally {
      setStrategyLoading(false);
    }
  };

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar
        position="sticky"
        elevation={0}
        sx={{
          background: 'linear-gradient(135deg, #0F1530 0%, #1A2148 55%, #232B5C 100%)',
          borderBottom: '1px solid rgba(255,255,255,0.08)',
        }}
      >
        <Toolbar sx={{ py: 1 }}>
          <Box sx={{ flexGrow: 1 }}>
            <Typography
              variant="h6"
              component="h1"
              sx={{
                fontFamily: '"Space Grotesk", sans-serif',
                fontWeight: 700,
                letterSpacing: '0.02em',
                color: '#FFFFFF',
                lineHeight: 1.2,
              }}
            >
              ZipRun Ops
            </Typography>
            <Typography
              sx={{
                fontFamily: '"Inter", sans-serif',
                fontSize: '0.72rem',
                fontWeight: 500,
                letterSpacing: '0.08em',
                textTransform: 'uppercase',
                color: '#F2A93B',
              }}
            >
              Delivery reassignment control center
            </Typography>
          </Box>
          <RoutingStrategyToggle
            strategy={routingStrategy}
            loading={strategyLoading}
            onChange={handleStrategyChange}
          />
          <Button
            color="inherit"
            onClick={handleRefresh}
            startIcon={
              <RefreshIcon
                sx={{
                  animation: loading ? 'spin 0.8s linear infinite' : 'none',
                }}
              />
            }
            sx={{ ml: 1, color: '#FFFFFF' }}
          >
            Refresh now
          </Button>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ py: 3 }}>
        {error && (
          <Alert
            severity="error"
            sx={{ mb: 2 }}
            onClose={() => setError(null)}
            action={
              <Button color="inherit" size="small" onClick={handleRefresh}>
                Retry
              </Button>
            }
          >
            {error}
          </Alert>
        )}

        {lastUpdated && (
          <Box
            sx={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 0.75,
              px: 1.25,
              py: 0.5,
              mb: 2,
              borderRadius: 5,
              bgcolor: 'rgba(35, 43, 92, 0.06)',
              border: '1px solid',
              borderColor: 'divider',
            }}
          >
            <Box
              sx={{
                width: 7,
                height: 7,
                borderRadius: '50%',
                bgcolor: 'success.main',
                animation: 'pulse 1.8s ease-in-out infinite',
              }}
            />
            <Typography
              variant="caption"
              sx={{ color: 'text.secondary', fontWeight: 500 }}
            >
              Last updated {lastUpdated.toLocaleTimeString()}
            </Typography>
            <Typography
              variant="caption"
              sx={{ color: 'text.disabled' }}
            >
              · auto-refreshes every 5s
            </Typography>
          </Box>
        )}

        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', lg: '1fr 1.2fr' },
            alignItems: 'start',
            gap: 3,
            mb: 3,
          }}
        >
          <AgentsPanel agents={agents} loading={loading} onRefresh={handleRefresh} />
          <SuggestionsPanel
            suggestions={suggestions}
            agents={agents}
            loading={loading}
            onResolved={handleSuggestionResolved}
          />
        </Box>

        <OrdersPanel
          orders={orders}
          agents={agents}
          loading={loading}
          statusFilter={orderStatusFilter}
          onStatusFilterChange={setOrderStatusFilter}
        />
      </Container>

      <Snackbar
        open={Boolean(toast)}
        autoHideDuration={3000}
        onClose={() => setToast(null)}
        message={toast}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      />
    </Box>
  );
}

export default Dashboard;