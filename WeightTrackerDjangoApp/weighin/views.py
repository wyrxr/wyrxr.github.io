"""
Author: Nathaniel Ingle
Date: 20-July-2025

"""

import matplotlib
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import io
import base64
import weighin.utils as utils
from django.views.generic import ListView, DetailView, TemplateView
from django.views.generic.edit import CreateView, UpdateView, DeleteView
from weighin.models import WeighIn
from django.urls import reverse_lazy
from django.shortcuts import render
from django.contrib.auth.mixins import LoginRequiredMixin

matplotlib.use('agg')

# Create view for the complete weigh-in list.
class WeighInList(ListView):
    model = WeighIn
    template_name = "home.html"

    def get_queryset(self):
        if self.request.user.is_authenticated:
            return (
                WeighIn.objects.filter(user=self.request.user)
                .order_by("-date")
                .select_related("user")
            )
        return WeighIn.objects.none()


# View for individual weigh-ins
class WeighInDetail(DetailView):
    model = WeighIn
    template_name = "weighin_detail.html"

class WeighInCreate(LoginRequiredMixin, CreateView):
    model = WeighIn
    template_name = "weighin_new.html"
    fields = ["weight", "date"]
    success_url = reverse_lazy('home')

    def form_valid(self, form):
        form.instance.user = self.request.user
        return super().form_valid(form)

class WeighInUpdate(UpdateView):
    model = WeighIn
    template_name = "weighin_edit.html"
    fields = ["weight"]

class WeighInDelete(DeleteView):
    model = WeighIn
    template_name = "weighin_delete.html"
    success_url = reverse_lazy('home')

def chart_view(request):
    if not request.user.is_authenticated:
        return render(request, 'home.html')

    weighins = WeighIn.objects.filter(user=request.user).order_by("-date")

    if weighins.count() == 0:
        return render(request, 'home.html')

    time = [w.date for w in weighins]
    weights = [w.weight for w in weighins]

    # Create the plot
    plt.figure(figsize=(8, 6))
    plt.plot(time, weights, marker='o')
    plt.title('Weight Over Time')
    plt.xlabel('Date')
    plt.ylabel('Weight')
    plt.grid(True)


    # Linear regression
    # equation, y = mx + b
    x_line = mdates.date2num(time)
    m, b = utils.simple_linear_regression(x_line, weights)
    y_line = [m * xi + b for xi in x_line]

    plt.plot(x_line, y_line, color='red')

    # Save plot to a BytesIO object
    buffer = io.BytesIO()
    plt.savefig(buffer, format='png')
    buffer.seek(0)
    plt.close() # Close the figure to free up memory

    # Encode to base64
    image_png = buffer.getvalue()
    graphic = base64.b64encode(image_png)
    graphic = graphic.decode('utf-8')

    context = {'graphic': graphic}
    return render(request, 'weighin_graph.html', context)
