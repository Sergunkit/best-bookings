import { test, expect } from '@playwright/test';

const AMENITIES_ENDPOINT = 'https://hexling.ru/api/amenities ';

test.describe('API тесты для /amenities', () => {
  let response;

  test.beforeEach(async ({ request }) => {
    const apiResponse = await request.get(AMENITIES_ENDPOINT);
    response = {
      status: apiResponse.status(),
      body: await apiResponse.json(),
      headers: apiResponse.headers()
    };
  });

  test('Должен возвращать статус 200', () => {
    expect(response.status).toBe(200);
  });

  test('Должен возвращать массив удобств', () => {
    expect(Array.isArray(response.body)).toBeTruthy();
    expect(response.body.length).toBeGreaterThan(0);
  });

  test('Каждый элемент должен соответствовать интерфейсу Amenity', () => {
    response.body.forEach((amenity) => {
      expect(amenity).toEqual({
        id: expect.any(Number),
        name: expect.any(String)
      });
    });
  });

  test('Должен содержать обязательные удобства', () => {
    const requiredAmenities = ['Wi-Fi', 'Кондиционер', 'Телевизор'];
    requiredAmenities.forEach((amenityName) => {
      expect(response.body.some((a) => a.name === amenityName)).toBeTruthy();
    });
  });

  test('ID должны быть уникальными', () => {
    const ids = response.body.map((a) => a.id);
    const uniqueIds = new Set(ids);
    expect(ids.length).toBe(uniqueIds.size);
  });

  test('Названия не должны быть пустыми', () => {
    response.body.forEach((amenity) => {
      expect(amenity.name.trim().length).toBeGreaterThan(0);
    });
  });

  test('Время ответа должно быть < 300мс', async ({ request }) => {
    const startTime = Date.now();
    await request.get(AMENITIES_ENDPOINT);
    const duration = Date.now() - startTime;
    expect(duration).toBeLessThan(300);
  });

  test('Должен возвращать Content-Type application/json', () => {
    expect(response.headers['content-type']).toContain('application/json');
  });
});