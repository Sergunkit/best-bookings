import { DefaultApi, Configuration } from '@generatedClient/index';

const basePath = process.env.NODE_ENV === 'development'
  ? 'http://localhost:8000/api'
  : 'https://hexling.ru/api';

const config = new Configuration({
  basePath,
  fetchApi: async (url, init) => {
    const token = localStorage.getItem('token');
    const headers = new Headers(init?.headers || {});
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }

    return fetch(url, {
      ...init,
      headers,
    });
  },
});
const client = new DefaultApi(config);

export { basePath };
export * from '@generatedClient/index';

export default client;
