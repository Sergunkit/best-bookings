import "@typespec/http";
import "@typespec/openapi";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

@service({
  title: "Hotel Booking API",
})

@server("https://example.com", "Single server endpoint")

namespace HotelBooking;

model CommonParams {
  @query page?: int32 = 1;
  @query perPage?: int32 = 24;
  @query sortBy?: string;
  @query sortOrder?: "ASC" | "DESC" = "ASC";
}

@error
model ProblemDetails {
  title?: string;
  detail?: string;
}

@error
model NotFoundError {
  @statusCode _: 404;
  ...ProblemDetails;
}

@error
model UnauthorizedError {
  @statusCode _: 401;
  ...ProblemDetails;
}

@error
model ForbiddenError {
  @statusCode _: 403;
  ...ProblemDetails;
}

@error
model UnprocessableEntityError {
  @statusCode _: 422;
  ...ProblemDetails;
  errors: Array<{
    message: string;
    field: string;
    rule: string;
  }>;
}

model Hotel {
  @example(1)
  id: int32;
  @example("Hilton")
  name: string;
  @example("Описание отеля")
  description: string;
  @example(5)
  rating: float;
  @example(5)
  stars: int32;
  photoSrc: string;
}

model HotelCreateDto {
  name: string;
  description: string;
  rating: float;
  stars: int32;
  photoSrc: string;
}

model HotelUpdateDto {
  name?: string;
  description?: string;
  rating?: float;
  stars?: int32;
  photoSrc?: string;
}

model HotelFilters {
  name?: string;
  minRating?: float;
  maxRating?: float;
  minStars?: int32;
  maxStars?: int32;
}

alias HotelSortOptions = "name" | "rating" | "stars";

model HotelParams extends CommonParams {
  @query name?: string;
  @query minRating?: float;
  @query maxRating?: float;
  @query minStars?: int32;
  @query maxStars?: int32;
  @query sortBy?: HotelSortOptions = "name";
}

model Amenity {
  id: int32;
  name: string;
}

model AmenityCreateDto {
  name: string;
}

model AmenityUpdateDto {
  name: string;
}

model RoomAmenity {
  id: int32;
  roomId: int32;
  amenityId: int32;
}

model RoomAmenityDto {
  roomId: int32;
  amenityId: int32;
}

model RoomAmenityCreateDto {
  roomId: int32;
  amenityId: int32;
}

model Room {
  @example(1)
  id: int32;
  @example("Люкс с видом на колизей")
  name: string;
  @example("Описание люкса с видом на колизей")
  description: string;
  @example(300)
  price: float;
  @example(1)
  hotelId: int32;
}

model RoomCreateDto {
  name: string;
  description: string;
  price: float;
  hotelId: int32;
}

model RoomUpdateDto {
  name?: string;
  description?: string;
  price?: float;
}

model RoomDto {
  ...Room;
  amenities: Amenity[];
}

model RoomFilters {
  name?: string;
  minPrice?: float;
  maxPrice?: float;
}

alias RoomSortOptions = "name" | "price";

model RoomParams extends CommonParams {
  @query name?: string;
  @query minPrice?: float;
  @query maxPrice?: float;
  @query sortBy?: RoomSortOptions = "name";
}

model User {
  id: int32;
  name: string;
  email: string;
  password: string;
}

model UserDto {
  id: int32;
  name: string;
  email: string;
}

model UserCreateDto {
  name: string;
  email: string;
  password: string;
}

model UserUpdateDto {
  name?: string;
  email?: string;
  password?: string;
}

model UserFilters {
  name?: string;
  email?: string;
}

alias UserSortOptions = "name" | "email";

model UserParams extends CommonParams {
  @query name?: string;
  @query email?: string;
  @query sortBy?: UserSortOptions = "name";
}

model Booking {
  id: int32;
  userId: int32;
  roomId: int32;
  checkIn: utcDateTime;
  checkOut: utcDateTime;
}

model BookingCreateDto {
  userId: int32;
  roomId: int32;
  checkIn: utcDateTime;
  checkOut: utcDateTime;
}

model BookingUpdateDto {
  userId?: int32;
  roomId?: int32;
  checkIn?: utcDateTime;
  checkOut?: utcDateTime;
}

model BookingFilters {
  userId?: int32;
  roomId?: int32;
  checkInBefore?: utcDateTime;
  checkInAfter?: utcDateTime;
  checkOutBefore?: utcDateTime;
  checkOutAfter?: utcDateTime;
}

alias BookingSortOptions = "userId" | "roomId" | "checkIn" | "checkOut";

model BookingParams extends CommonParams {
  @query userId?: int32;
  @query roomId?: int32;
  @query checkInBefore?: utcDateTime;
  @query checkInAfter?: utcDateTime;
  @query checkOutBefore?: utcDateTime;
  @query checkOutAfter?: utcDateTime;
  @query sortBy?: BookingSortOptions = "checkIn";
}

model Favorite {
  id: int32;
  userId: int32;
  hotelId: int32;
}

model FavoriteCreateDto {
  hotelId: int32;
}

model RoomAvailableDatesParams {
  @query start: plainDate;
  @query end: plainDate;
}

model RoomAvailableDates {
  roomId: int32;
  dates: plainDate[];
}

model PaginatedDto<Data, Filters, SortBy> {
  data: Data;
  pagination: {
    page: int32;
    perPage: int32;
    totalPages: int32;
    total: int32;
  };
  filters: Filters;
  sortBy: SortBy;
  sortOrder: "ASC" | "DESC";
}

model AuthInfo {
  email: string;
  password: string;
}

model TokenInfo {
  token: string;
}

model Message {
  id: int32;
  senderId: int32;
  receiverId: int32;
  content: string;
  createdAt: utcDateTime;
}

model MessageCreateDto {
  senderId: int32;
  receiverId: int32;
  content: string;
}

@route("/hotels")
namespace Hotels {
  @get
  op list(...HotelParams): {
    @statusCode statusCode: 200;
    @body hotels: PaginatedDto<Hotel[], HotelFilters, HotelSortOptions>;
  };

  @get
  op get(@path hotelId: int32): {
    @statusCode statusCode: 200;
    @body hotel: Hotel;
  } | NotFoundError;

  @post
  @useAuth(BearerAuth)
  op create(@body hotel: HotelCreateDto): {
    @statusCode statusCode: 201;
    @body newHotel: Hotel;
  } | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @patch
  @useAuth(BearerAuth)
  op update(@path hotelId: int32, @body hotel: HotelUpdateDto): {
    @statusCode statusCode: 200;
    @body updatedHotel: Hotel;
  } | NotFoundError | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @delete
  @useAuth(BearerAuth)
  op delete(@path hotelId: int32): {
    @statusCode statusCode: 204;
  } | NotFoundError | UnauthorizedError | ForbiddenError;
}

@route("/amenities")
namespace Amenities {
  @get
  op list(): {
    @statusCode statusCode: 200;
    @body amenities: Amenity[];
  };

  @post
  @useAuth(BearerAuth)
  op create(@body amenity: AmenityCreateDto): {
    @statusCode statusCode: 201;
    @body newAmenity: Amenity;
  } | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @patch
  @useAuth(BearerAuth)
  op update(@path amenityId: int32, @body amenity: AmenityUpdateDto): {
    @statusCode statusCode: 200;
    @body updatedAmenity: Amenity;
  } | NotFoundError | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @delete
  @useAuth(BearerAuth)
  op delete(@path amenityId: int32): {
    @statusCode statusCode: 204;
  } | NotFoundError | UnauthorizedError | ForbiddenError;
}

@route("/rooms")
namespace Rooms {
  @get
  op list(...RoomParams): {
    @statusCode statusCode: 200;
    @body rooms: PaginatedDto<RoomDto[], RoomFilters, RoomSortOptions>;
  };

  @get
  op get(@path roomId: int32): {
    @statusCode statusCode: 200;
    @body room: RoomDto;
  } | NotFoundError;
}

@route("/rooms/{roomId}/availability")
namespace RoomAvailability {
  @get
  op get(@path roomId: int32, ...RoomAvailableDatesParams): {
    @statusCode statusCode: 200;
    @body availableDates: RoomAvailableDates;
  } | NotFoundError | UnprocessableEntityError;
}

@route("/hotels/{hotelId}/rooms")
namespace HotelRooms {
  @get
  op list(@path hotelId: int32, ...RoomParams): {
    @statusCode statusCode: 200;
    @body rooms: PaginatedDto<RoomDto[], RoomFilters, RoomSortOptions>;
  } | NotFoundError;

  @post
  @useAuth(BearerAuth)
  op create(@path hotelId: int32, @body room: RoomCreateDto): {
    @statusCode statusCode: 201;
    @body newRoom: RoomDto;
  } | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @patch
  @useAuth(BearerAuth)
  op update(@path hotelId: int32, @path roomId: int32, @body room: RoomUpdateDto): {
    @statusCode statusCode: 200;
    @body updatedRoom: RoomDto;
  } | NotFoundError | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @delete
  @useAuth(BearerAuth)
  op delete(@path hotelId: int32, @path roomId: int32): {
    @statusCode statusCode: 204;
  } | NotFoundError | UnauthorizedError | ForbiddenError;
}

@route("/hotels/{hotelId}/rooms/{roomId}/amenities")
@useAuth(BearerAuth)
namespace RoomAmenities {
  @post
  op add(@path hotelId: int32, @path roomId: int32, @body roomAmenity: RoomAmenityCreateDto): {
    @statusCode statusCode: 201;
    @body addedAmenity: RoomAmenityDto;
  } | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @delete
  op delete(@path hotelId: int32, @path roomId: int32, @path amenityId: int32): {
    @statusCode statusCode: 204;
  } | NotFoundError | UnauthorizedError | ForbiddenError;
}

@route("/users")
namespace Users {
  @get
  @useAuth(BearerAuth)
  op list(...UserParams): {
    @statusCode statusCode: 200;
    @body users: PaginatedDto<UserDto[], UserFilters, UserSortOptions>;
  } | UnauthorizedError | ForbiddenError;

  @get
  @useAuth(BearerAuth)
  op get(@path userId: int32): {
    @statusCode statusCode: 200;
    @body user: UserDto;
  } | NotFoundError | UnauthorizedError | ForbiddenError;

  @post
  op create(@body user: UserCreateDto): {
    @statusCode statusCode: 201;
    @body newUser: UserDto;
  } | UnprocessableEntityError;

  @patch
  @useAuth(BearerAuth)
  op update(@path userId: int32, @body user: UserUpdateDto): {
    @statusCode statusCode: 200;
    @body updatedUser: UserDto;
  } | NotFoundError | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @delete
  @useAuth(BearerAuth)
  op delete(@path userId: int32): {
    @statusCode statusCode: 204;
  } | NotFoundError | UnauthorizedError | ForbiddenError;
}

@route("/users/me")
@useAuth(BearerAuth)
namespace UserMe {
  @get
  op get(): {
    @statusCode statusCode: 200;
    @body user: UserDto;
  } | UnauthorizedError;
}

@route("/users/{userId}/bookings")
@useAuth(BearerAuth)
namespace UserBookings {
  @get
  op list(@path userId: int32, ...OmitProperties<BookingParams, "userId">): {
    @statusCode statusCode: 200;
    @body bookings: PaginatedDto<Booking[], BookingFilters, BookingSortOptions>;
  } | UnauthorizedError | ForbiddenError;
}

@route("/users/{userId}/favorites")
@useAuth(BearerAuth)
namespace UserFavorites {
  @get
  @operationId("listUserFavorites")
  op list(@path userId: int32, ...HotelParams): {
    @statusCode statusCode: 200;
    @body favorites: PaginatedDto<Hotel[], HotelFilters, HotelSortOptions>;
  } | UnauthorizedError | ForbiddenError;
}

@route("/bookings")
@useAuth(BearerAuth)
namespace Bookings {
  @get
  op get(@path bookingId: int32): {
    @statusCode statusCode: 200;
    @body booking: Booking;
  } | NotFoundError | ForbiddenError;

  @post
  op create(@body booking: BookingCreateDto): {
    @statusCode statusCode: 201;
    @body newBooking: Booking;
  } | UnprocessableEntityError | ForbiddenError;

  @patch
  op update(@path bookingId: int32, @body booking: BookingUpdateDto): {
    @statusCode statusCode: 200;
    @body updatedBooking: Booking;
  } | NotFoundError | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @delete
  op delete(@path bookingId: int32): {
    @statusCode statusCode: 204;
  } | NotFoundError | ForbiddenError;
}

@route("/favorites")
@useAuth(BearerAuth)
namespace Favorites {
  @post
  @operationId("createFavorite")
  op create(@body favorite: FavoriteCreateDto): {
    @statusCode statusCode: 201;
    @body newFavorite: Hotel;
  } | UnprocessableEntityError | UnauthorizedError | ForbiddenError;

  @delete
  @operationId("deleteFavorite")
  op delete(@path hotelId: int32): {
    @statusCode statusCode: 204;
  } | NotFoundError | UnauthorizedError | ForbiddenError;
}

@route("/tokens")
namespace tokens {
  @post
  op create(@body authInfo: AuthInfo): {
    @body token: TokenInfo;
    @statusCode statusCode: 201;
  } | UnprocessableEntityError;
}

@route("/messages")
@useAuth(BearerAuth)
namespace Messages {
  @get
  op list(): {
    @statusCode statusCode: 200;
    @body messages: Message[];
  } | UnauthorizedError | ForbiddenError;

  @get
  op get(@path userId: int32): {
    @statusCode statusCode: 200;
    @body messages: Message[];
  } | NotFoundError | UnauthorizedError | ForbiddenError;

  @post
  op create(@body message: MessageCreateDto): {
    @statusCode statusCode: 201;
    @body newMessage: Message;
  } | UnprocessableEntityError | UnauthorizedError | ForbiddenError;
}
