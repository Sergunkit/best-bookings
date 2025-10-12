import { test, expect } from '@playwright/test';
//include
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

test('3.2.2 - Защита от XSS', async ({ page }) => {
  const testPayloads = [
    '<script>alert("XSS")</script>',
    '<img src="x" onerror="alert(\'XSS\')">',
    '<svg/onload=alert("XSS")>'
  ];

  await page.goto(testData.page);

  const searchInput = page.locator('input[placeholder="Поиск по названию отеля"]');
  await expect(searchInput).toBeVisible();


  for (const payload of testPayloads) {
    await searchInput.fill(payload);
    await page.keyboard.press('Enter');
    await page.waitForLoadState('networkidle');

    await expect(page.locator('body')).not.toContainText(payload);

    let dialogDetected = false;
    page.on('dialog', dialog => {
      dialogDetected = true;
      dialog.dismiss();
    });

    await page.waitForTimeout(500);
    expect(dialogDetected, `Обнаружен XSS через ${payload}`).toBe(false);
  }
});