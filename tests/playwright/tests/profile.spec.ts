import { test, expect } from '../fixtures/auth.fixture';
import parser from '../utils/parser.js';

const testData = parser('./utils/auth/user.json');

test('Должен отображать элементы профиля', async ({ page }) => {
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Профиль' }).click();
  await page.waitForLoadState('networkidle');
  await expect(page.getByRole('heading', { name: testData.user.name })).toBeVisible();
  await expect(page.getByText('Личные данные')).toBeVisible();
  await expect(page.getByText('Безопасность')).toBeVisible();
  await expect(page.getByRole('button', { name: 'delete Удалить аккаунт' })).toBeVisible();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Выход' }).click();
});
