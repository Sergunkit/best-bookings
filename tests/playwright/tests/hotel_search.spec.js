import { test, expect } from '../fixtures/auth.fixture';

test('Проверка поиска отелей', async ({ page }) => {
  await expect(page.locator('input[placeholder="Поиск по названию отеля"]')).toBeVisible();
  await page.locator('input[placeholder="Поиск по названию отеля"]').fill('Премиум');
  await expect(page.locator('input[placeholder="Поиск по названию отеля"]')).toHaveValue('Премиум');
  await expect(page.locator('.ant-input-clear-icon')).toBeVisible();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Выход' }).click();
});

