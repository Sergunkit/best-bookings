import { test, expect } from '@playwright/test';

test('Скорость загрузки страницы (<4 сек)', async ({ page}) => {
  const startTime = Date.now();
  await page.goto('https://hexling.ru/ ', { waitUntil: 'domcontentloaded' });
  const loadTime = Date.now() - startTime;
  expect(loadTime).toBeLessThan(4000);
});