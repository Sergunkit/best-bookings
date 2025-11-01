from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response

class CustomPagination(PageNumberPagination):
    page_size_query_param = 'perPage'

    def get_paginated_response(self, data):
        if not hasattr(self, 'page') or not self.page:
            return Response({
                'data': [],
                'pagination': {
                    'page': 1,
                    'perPage': self.get_page_size(self.request),
                    'totalPages': 0,
                    'total': 0,
                }
            })
        return Response({
            'data': data,
            'pagination': {
                'page': self.page.number,
                'perPage': self.page.paginator.per_page,
                'totalPages': self.page.paginator.num_pages,
                'total': self.page.paginator.count,
            }
        })