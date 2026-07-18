import { useCallback, useEffect, useRef } from 'react';

export function usePolling(callback, intervalMs = 5000, deps = []) {
  const savedCallback = useRef(callback);

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  const tick = useCallback(() => {
    savedCallback.current();
  }, []);

  useEffect(() => {
    tick();
    const id = setInterval(tick, intervalMs);
    return () => clearInterval(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps -- deps intentionally trigger re-fetch
  }, [tick, intervalMs, ...deps]);
}
