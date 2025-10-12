/* eslint-disable @typescript-eslint/no-floating-promises */
/* eslint-disable react/prop-types */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable max-len */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router';
import {
  Flex,
  Pagination,
  Select,
  Input,
  Space,
} from 'antd';
import client from '@api';
import type { Hotel } from '@api';
import HotelCard from '../src/components/hotelCard';
import { useAuth } from '../authContext';

type Pagination = {
  page: number;
  perPage: number;
  total: number;
};

type HotelWithFavorites = Hotel & { isFavorite: boolean };

export async function clientLoader() {
  const userData = localStorage.getItem('user');
  if (userData) {
    const user = JSON.parse(userData);
    const request = { userId: Number(user.id) };
    const response = await client.listUserFavorites(request);
    const favHotels = response.data.map((hotel: Hotel): HotelWithFavorites => ({ ...hotel, isFavorite: true }));
    return favHotels;
  }
  return null;
}

export type FavoriteHotelsProps = {
    loaderData: HotelWithFavorites[]
  };

const FavoriteHotels: React.FC<FavoriteHotelsProps> = ({
  loaderData,
}) => {
  const [hotels, setHotels] = useState<HotelWithFavorites[]>(loaderData);
  const [pagination, setPagination] = useState<Pagination>({
    page: 1,
    perPage: 10,
    total: 0,
  });
  const navigate = useNavigate();
  const location = useLocation();

  const { user } = useAuth();

  const handlePaginationChange = (page: number, pageSize: number) => {
    setPagination((prev) => ({
      ...prev,
      page,
      perPage: pageSize,
    }));
  };

  const toggleFavorite = async (hotelId: number) => {
    if (!user) {
      navigate('/signin', { state: { prevPath: location.pathname } });
      return;
    }

    const currentHotel = hotels.find((hotel) => hotel.id === hotelId)!;

    if (currentHotel.isFavorite) {
      await client.deleteFavorite({ hotelId });
    } else {
      await client.createFavorite({ favoriteCreateDto: { hotelId } });
    }

    setHotels((prevHotels) => prevHotels.map((hotel) => (
      hotel.id === hotelId ? { ...hotel, isFavorite: !hotel.isFavorite } : hotel
    )).filter((hotel) => hotel.isFavorite));
  };

  const handleSortChange = (value: string) => {
    const [sortBy, sortOrder] = value.split('_');
    const sortedHotels = [...hotels].sort((a, b) => {
      let comparison = 0;
      if (sortBy === 'name') {
        comparison = a.name.localeCompare(b.name);
      } else if (sortBy === 'rating') {
        comparison = a.rating - b.rating;
      } else if (sortBy === 'stars') {
        comparison = a.stars - b.stars;
      }
      return sortOrder === 'DESC' ? comparison * -1 : comparison;
    });

    setHotels(sortedHotels);
    setPagination((prev) => ({ ...prev, page: 1 }));
  };

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    const query = e.target.value.toLowerCase();
    if (query) {
      const filteredHotels = hotels.filter((hotel) => hotel.name.toLowerCase().includes(query));
      setHotels(filteredHotels);
    } else {
      setHotels(loaderData);
    }
    setPagination((prev) => ({ ...prev, page: 1 }));
  };

  return (
    <Flex gap={20}>
      <Flex vertical style={{ flex: 1 }} gap={20}>
        <Input
          placeholder="Поиск по названию отеля"
          size="large"
          allowClear
          onChange={handleSearch}
          style={{ maxWidth: 500 }}
        />

        <Select
          placeholder="Сортировать по"
          style={{ width: 300 }}
          options={[
            { value: 'name_ASC', label: 'Названию (А-Я)' },
            { value: 'name_DESC', label: 'Названию (Я-А)' },
            { value: 'rating_ASC', label: 'Рейтингу (↑)' },
            { value: 'rating_DESC', label: 'Рейтингу (↓)' },
            { value: 'stars_ASC', label: 'Звездам (↑)' },
            { value: 'stars_DESC', label: 'Звездам (↓)' },
          ]}
          onChange={handleSortChange}
        />
        <Space direction="vertical">
          <Flex wrap="wrap" gap={20} style={{ width: '70%' }}>
            {hotels.map((hotel) => (
              <HotelCard
                key={hotel.id}
                name={hotel.name}
                description={hotel.description}
                stars={hotel.stars}
                rating={hotel.rating}
                id={hotel.id}
                isFavorite={hotel.isFavorite}
                photoSrc={hotel.photoSrc}
                toggleFavorite={toggleFavorite}
              />
            ))}
          </Flex>

          <Pagination
            current={pagination.page}
            pageSize={pagination.perPage}
            total={pagination.total}
            onChange={handlePaginationChange}
            style={{ marginTop: 20 }}
            showSizeChanger={{
              options: [
                { value: 10, label: '10 на странице' },
                { value: 20, label: '20 на странице' },
                { value: 50, label: '50 на странице' },
              ],
            }}
          />
        </Space>
      </Flex>
    </Flex>
  );
};

export default FavoriteHotels;
