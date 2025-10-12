import fs from 'fs';

/**
 * Читает JSON-файл по указанному пути и возвращает объект.
 * @param path - путь к JSON-файлу
 */
export default function readJSONFile<T = any>(path: string): T {
  const rawData = fs.readFileSync(path, 'utf8');
  return JSON.parse(rawData);
}