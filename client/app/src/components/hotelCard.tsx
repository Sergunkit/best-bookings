/* eslint-disable no-nested-ternary */
/* eslint-disable react/jsx-indent */
/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-unsafe-assignment */
import { Link } from 'react-router-dom';
import {
  Card, Rate, Typography, Image, Tag, Flex, Button, Grid,
} from 'antd';
import { StarFilled, HeartFilled, HeartOutlined } from '@ant-design/icons';

const { useBreakpoint } = Grid;
const { Title, Text } = Typography;

type HotelCardProps = {
  id: number;
  name: string;
  stars: number;
  description: string;
  rating: number;
  isFavorite: boolean;
  photoSrc: string;
  toggleFavorite: (hotelId: number) => void;
};

const HotelCard = ({
  id,
  name,
  stars,
  description,
  rating,
  isFavorite,
  photoSrc,
  toggleFavorite,
}: HotelCardProps) => {
  const ratingColor = () => {
    if (rating >= 8) return '#52c41a';
    if (rating >= 6) return '#faad14';
    return '#ff4d4f';
  };
  const { md } = useBreakpoint();
  const handleFavoriteClick = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    toggleFavorite(id);
  };

  return (
<Card
  hoverable
  style={{
    width: '100%',
    borderRadius: 8,
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
    marginBottom: 16,
  }}
>
      <Link to={`/hotels/${String(id)}`}>
        <Flex vertical={!md} gap={16} align="start">
          <div style={{ position: 'relative', width: md ? 240 : '100%' }}>
            <Image
              alt={name}
              src={`/hotel-images/${photoSrc}`}
              width="100%"
              height={180}
              style={{
                borderRadius: 4,
                objectFit: 'cover',
              }}
              preview={false}
            />
            <Button
              type="text"
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                handleFavoriteClick(e);
              }}
              style={{
                position: 'absolute',
                top: 8,
                left: 8,
                zIndex: 2,
                padding: 4,
                background: 'rgba(255, 255, 255, 0.8)',
                borderRadius: '50%',
                width: 32,
                height: 32,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
              aria-label={isFavorite ? 'Удалить из избранного' : 'Добавить в избранное'}
            >
              {isFavorite ? (
                <HeartFilled style={{ fontSize: 18, color: 'red' }} />
              ) : (
                <HeartOutlined style={{ fontSize: 18, color: '#bfbfbf' }} />
              )}
            </Button>
          </div>
          <Flex vertical style={{ flex: 1 }} gap={12}>
            <Flex justify="space-between" align="start">
              <Title level={4} style={{ margin: 0 }}>
                {name}
              </Title>
              <Tag color={ratingColor()} style={{ fontSize: 16, padding: '4px 8px' }}>
                {rating.toFixed(1)}
              </Tag>
            </Flex>
            <Flex gap={8}>
              <Rate
                character={<StarFilled />}
                value={stars}
                count={5}
                disabled
              />
              <Text type="secondary">
                {stars} {stars === 1 ? 'звезда' : stars < 5 ? 'звезды' : 'звёзд'}
              </Text>
            </Flex>
            <Text
              style={{
                color: '#595959',
                fontSize: 15,
                lineHeight: 1.5,
                height: '135px',
                overflow: 'hidden',
              }}
              ellipsis={{ expanded: true }}
            >
              {description}
            </Text>
          </Flex>
        </Flex>
      </Link>
</Card>
  );
};

export default HotelCard;
