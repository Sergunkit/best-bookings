import { test, expect } from '../fixtures/auth.fixture';

test('Добавление в избранное', async ({ page }) => {
  await page.getByRole('button', { name: 'Добавить в избранное' }).first().click();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Любимые отели' }).click();
  await page.waitForLoadState('networkidle');
  await page.getByRole('button', { name: 'Удалить из избранного' }).first().click();
  await page.waitForTimeout(500);
  await expect(page.locator('span.anticon anticon-heart').first())
  .not.toBeVisible({ timeout: 15000 });
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Выход' }).click();
});
