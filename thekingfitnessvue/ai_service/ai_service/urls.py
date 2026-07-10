from django.urls import path
from predict.views import predict_weight

urlpatterns = [
    path('predict/weight/', predict_weight),
]
