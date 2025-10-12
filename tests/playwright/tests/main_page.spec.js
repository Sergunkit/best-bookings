import { test, expect } from '../fixtures/auth.fixture';

test('Отображение основных элементов страницы', async ({ page }) => {
  await expect(page.locator('header >> text=HEXLING')).toBeVisible();
  await expect(page.locator('header img[src="/logo.png"]')).toBeVisible();
  await expect(page.locator('.ant-card-head-title:has-text("Все фильтры")')).toBeVisible();
  await expect(page.locator('input[placeholder="Поиск по названию отеля"]')).toBeVisible();
  const hotelCards = page.locator('.ant-card-hoverable');
  await expect(hotelCards.first()).toBeVisible();
  expect(await hotelCards.count()).toBeGreaterThan(0);
  await expect(page.locator('.ant-pagination-item-active >> text=1')).toBeVisible();
  await expect(page.locator('.ant-select-selection-placeholder:has-text("Мин. звезд")')).toBeVisible();
  await expect(page.locator('.ant-select-selection-placeholder:has-text("Макс. звезд")')).toBeVisible();
  await expect(page.locator('.ant-slider')).toBeVisible();
  await expect(page.locator('button:has-text("Сбросить")')).toBeVisible()
  await expect(page.locator('button:has-text("Применить")')).toBeVisible();
  await page.getByRole('button', { name: 'user Профиль' }).click();
  await page.getByRole('link', { name: 'Выход' }).click();
});