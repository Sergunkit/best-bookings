import { test, expect } from '../fixtures/auth.fixture';

test('Нельзя забронировать на недоступную дату', async ({ page }) => {
  await page.waitForLoadState('networkidle');
  await page.click('a[href="/hotels/11"]');
  await page.getByRole('button', { name: 'Забронировать' }).first().click();
  await page.getByRole('textbox', { name: 'Start date' }).click();
  await page.locator('div.ant-picker-input > input[date-range="start"]').fill('24.08.2025');
  await page.locator('div.ant-picker-input > input[date-range="end"]').fill('26.08.2025');
  await page.keyboard.press('Enter');
  await page.keyboard.press('Enter');
  await expect(page.getByText('Field validation error for dates')).toBeVisible();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Выход' }).click();
});
