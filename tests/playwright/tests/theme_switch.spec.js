import { test, expect } from '../fixtures/auth.fixture';

test('Проверка переключателя темы', async ({ page }) => {
  const themeSwitch = page.locator('.ant-switch');
  await expect(themeSwitch).toBeVisible();
  await expect(themeSwitch).toHaveClass(/ant-switch-checked/);
  await themeSwitch.click();
  await expect(themeSwitch).not.toHaveClass(/ant-switch-checked/);
});
