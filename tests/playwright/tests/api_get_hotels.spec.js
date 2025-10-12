import { test, expect } from '@playwright/test';

test('GET /hotels - проверка структуры ответа', async ({ request }) => {
  const response = await request.get('https://hexling.ru/api/hotels');
  expect(response.status()).toBe(200);
  const responseData = await response.json();
  expect(responseData).toHaveProperty('data');
  expect(responseData).toHaveProperty('pagination');
  expect(responseData).toHaveProperty('filters');
  expect(responseData).toHaveProperty('sortBy');
  expect(responseData).toHaveProperty('sortOrder');
});