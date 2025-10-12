import { test, expect } from '../fixtures/auth.fixture';

test('Отображение информации об отеле', async ({ page }) => {
  await page.waitForLoadState('networkidle');
  await page.click('a[href="/hotels/11"]');
  await expect(page.locator('h2')).toBeVisible();
  await expect(page.locator('div').filter({ hasText: /^Звёзды:$/ }).nth(1)).toBeVisible();
  await expect(page.getByText('Рейтинг:8.3')).toBeVisible();
  await expect(page.getByText('Доступные номера')).toBeVisible();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Выход' }).click();
});
