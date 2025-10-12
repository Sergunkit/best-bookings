import { test, expect } from '../fixtures/auth.fixture';

test('Бронирование', async ({ page }) => {
  await page.waitForLoadState('networkidle');
  await page.click('a[href="/hotels/11"]');
  await page.getByRole('button', { name: 'Забронировать' }).first().click();
  await page.getByRole('textbox', { name: 'Start date' }).click();
  await page.locator('div.ant-picker-input > input[date-range="start"]').fill('05.07.2025');
  await page.locator('div.ant-picker-input > input[date-range="end"]').fill('06.07.2025');
  await page.keyboard.press('Enter');
  await page.keyboard.press('Enter');
  await page.getByRole('button', { name: 'Подтвердить бронирование' }).click();
  await expect(page.getByRole('heading', { name: 'Бронирование подтверждено! 🎉' })).toBeVisible();
  await page.getByRole('button', { name: 'Перейти к списку бронирований' }).click();
  await page.getByRole('button', { name: 'Изменить даты' }).click();
  await expect(page.getByRole('heading', { name: 'Выберите даты проживания' })).toBeVisible();
  await page.getByRole('textbox', { name: 'End date' }).click();
  await page.locator('div.ant-picker-input > input[date-range="end"]').fill('08.07.2025');
  await page.keyboard.press('Enter');
  await page.keyboard.press('Enter');
  await page.getByRole('button', { name: 'Подтвердить бронирование' }).click();
  await expect(page.getByRole('heading', { name: 'Бронирование подтверждено! 🎉' })).toBeVisible();
  await page.getByRole('button', { name: 'Перейти к списку бронирований' }).click();
  await page.getByRole('button', { name: 'Удалить бронь' }).click();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'HEXLING' }).click();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Выход' }).click();
});
