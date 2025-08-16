from django.urls import path
from .views import (
    WeighInList,
    WeighInDetail,
    WeighInCreate,
    WeighInUpdate,
    WeighInDelete,
    chart_view
)

urlpatterns = [
    path("weighin/new/", WeighInCreate.as_view(), name="weighin_new"),
    path("weighin/<int:pk>/", WeighInDetail.as_view(), name="weighin_detail"),
    path("weighin/<int:pk>/edit", WeighInUpdate.as_view(), name="weighin_edit"),
    path("weighin/<int:pk>/delete", WeighInDelete.as_view(), name="weighin_delete"),
    path("weighin/graph", chart_view, name="weighin_graph"),
    path("", WeighInList.as_view(), name="home"),
]