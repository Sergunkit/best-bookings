import { test, expect } from '@playwright/test';

test('page loading speed', async ({ page }) => {
  await page.goto('https://unreal-rooms.ru/ ', { waitUntil: 'domcontentloaded' });
  const loadTime = await page.evaluate(() => performance.timing.domContentLoadedEventEnd - performance.timing.navigationStart);
  expect(loadTime).toBeLessThan(5000); // Ожидаем, что страница загрузится менее чем за 5 секунд
});