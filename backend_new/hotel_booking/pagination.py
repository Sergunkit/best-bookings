from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response

class CustomPagination(PageNumberPagination):
    page_size_query_param = 'perPage'

    def get_paginated_response(self, data):
        return Response({
            'data': data,
            'pagination': {
                'page': self.page.number,
                'perPage': self.page.paginator.per_page,
                'totalPages': self.page.paginator.num_pages,
                'total': self.page.paginator.count,
            }
        })