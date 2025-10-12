import { test, expect } from '@playwright/test';

import parser from '../utils/parser.js';

const useEnv = Boolean(process.env.TEST_USER_FOR && process.env.TEST_PASSWORD_FOR && process.env.URL_FOR);
let testData;

if (useEnv) {
  testData = {
    page: process.env.URL_FOR,
    user: {
      email: process.env.TEST_USER_FOR,
      valid_password: process.env.TEST_PASSWORD_FOR,
      invalid_password: '12344214',
    },
  };
} else {
  testData = parser('./utils/auth/user.json');
}

test('Авторизация с корректным паролем', async ({ page }) => {
  await page.goto(testData.page);
  await page.waitForLoadState('networkidle');
  await page.getByRole('button', { name: 'Войти' }).click();
  await page.locator('input[placeholder="Email"]').fill(testData.user.email);
  await page.locator('input[placeholder="Пароль"]').fill(testData.user.valid_password);
  await page.locator('form').getByRole('button', { name: 'Войти' }).click();
  await expect(page.getByRole('button', { name: 'user Профиль' })).toBeVisible();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Выход' }).click();
});
