from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response

class StandardResultsSetPagination(PageNumberPagination):
    page_size = 10
    page_size_query_param = 'perPage'
    max_page_size = 100

    def get_paginated_response(self, data):
        return Response({
            'data': data,
            'pagination': {
                'page': self.page.number,
                'perPage': self.get_page_size(self.request),
                'totalPages': self.page.paginator.num_pages,
                'total': self.page.paginator.count,
            },
            'filters': {},
            'sortBy': self.request.query_params.get('sortBy', 'name'),
            'sortOrder': self.request.query_params.get('sortOrder', 'ASC'),
        })
