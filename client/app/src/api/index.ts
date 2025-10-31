import { DefaultApi, Configuration } from '@generatedClient/index';

const basePath = process.env.NODE_ENV === 'development'
  ? 'http://localhost:8000/api'
  : 'https://hexling.ru/api';

const config = new Configuration({
  basePath,
  accessToken: () => {
    const token = localStorage.getItem('accessToken');
    return token || '';
  },
});
const client = new DefaultApi(config);

export { basePath };
export * from '@generatedClient/index';

export default client;
