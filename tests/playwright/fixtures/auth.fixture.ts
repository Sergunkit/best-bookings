import { test as base } from '@playwright/test';
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

export const test = base.extend({
  page: async ({ browser }, use) => {
    const context = await browser.newContext();
    const page = await context.newPage();
    await page.goto(testData.page);
    await page.waitForLoadState('networkidle');
    await page.locator('header > div > div > button.ant-btn').click();
    await page.waitForLoadState('networkidle');
    await page.locator('input[placeholder="Email"]').fill(testData.user.email);
    await page.locator('input[placeholder="Пароль"]').fill(testData.user.valid_password);
    await page.locator('form').getByRole('button', { name: 'Войти' }).click();
    await use(page);
    await context.close();
  },
});

export { expect } from '@playwright/test';