/* eslint-disable @typescript-eslint/no-unsafe-return */
/* eslint-disable @typescript-eslint/restrict-template-expressions */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
import {
  DefaultApi, Configuration, type Middleware, type ResponseContext, ResponseError,
} from '@generatedClient/index';

const getBasePath = () => {
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL;
  }
  if (import.meta.env.MODE === 'development') {
    return 'http://localhost:8000/api';
  }
  return 'https://hexling.ru/api';
};

const basePath = getBasePath();

interface FailedRequest {
  resolve: (value: string | null) => void;
  reject: (error: unknown) => void;
}

interface RefreshResponse {
  access: string;
  refresh?: string;
}

let isRefreshing = false;
let failedQueue: FailedRequest[] = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

const logout = () => {
  localStorage.removeItem('user');
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  window.location.href = '/signin';
};

const refreshTokenMiddleware: Middleware = {
  async post(context: ResponseContext): Promise<Response | undefined> {
    if (context.response.status !== 401) {
      return undefined;
    }

    if (isRefreshing) {
      return await new Promise<string | null>((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      })
        .then(async (token) => {
          if (context.init.headers && token) {
            (context.init.headers as Record<string, string>).Authorization = `Bearer ${token}`;
          }
          return await context.fetch(context.url, context.init);
        });
    }

    isRefreshing = true;
    const refreshToken = localStorage.getItem('refreshToken');

    if (!refreshToken) {
      isRefreshing = false;
      logout();
      return context.response;
    }

    try {
      const response = await fetch(`${basePath}/token/refresh/`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refresh: refreshToken }),
      });

      if (!response.ok) {
        throw new ResponseError(response, 'Refresh token failed');
      }

      const { access, refresh } = await response.json() as RefreshResponse;
      localStorage.setItem('accessToken', access);
      if (refresh) {
        localStorage.setItem('refreshToken', refresh);
      }

      if (context.init.headers) {
        (context.init.headers as Record<string, string>).Authorization = `Bearer ${access}`;
      }

      processQueue(null, access);
      return await context.fetch(context.url, context.init);
    } catch (error) {
      processQueue(error, null);
      logout();
      throw new Error('Token refresh failed', { cause: error instanceof Error ? error : undefined });
    } finally {
      isRefreshing = false;
    }
  },
};

const config = new Configuration({
  basePath,
  accessToken: () => localStorage.getItem('accessToken') || '',
  middleware: [refreshTokenMiddleware],
});

const client = new DefaultApi(config);

export { basePath };
export * from '@generatedClient/index';

export default client;
