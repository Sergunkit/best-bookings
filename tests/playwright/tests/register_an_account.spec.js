import { test, expect } from '@playwright/test';
import parser from '../utils/parser.js';

const useEnv = Boolean(process.env.URL_FOR);
let testData;

if (useEnv) {
  testData = {
    page: process.env.URL_FOR,
  };
} else {
  testData = parser('./utils/auth/user.json');
}

test('Должен регистрировать учетную запись', async ({ page }) => {
  const uniqueId = `${Date.now()}${Math.floor(Math.random() * 1000)}`
  await page.goto(testData.page);
  await page.waitForLoadState('networkidle');
  await expect(page.getByRole('button', { name: 'Войти' })).toBeVisible();
  await page.getByRole('button', { name: 'Войти' }).click();
  await expect(page.locator('[data-node-key="register"]')).toBeVisible();
  await page.locator('[data-node-key="register"]').click();
  await page.getByRole('textbox', { name: 'Имя' }).click();
  await page.getByRole('textbox', { name: 'Имя' }).fill(`testuser_1${uniqueId}`);
  await page.getByRole('textbox', { name: 'Email' }).click();
  await page.getByRole('textbox', { name: 'Email' }).fill(`testuser_1${uniqueId}@test.ru`);
  await page.getByRole('textbox', { name: 'Пароль' }).click();
  await page.getByRole('textbox', { name: 'Пароль' }).fill('testpassword');
  await page.getByRole('button', { name: 'Зарегистрироваться' }).click();
  await page.waitForLoadState('networkidle');
  await expect(page.getByRole('button', { name: 'user Профиль' })).toBeVisible();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Выход' }).click();
});
