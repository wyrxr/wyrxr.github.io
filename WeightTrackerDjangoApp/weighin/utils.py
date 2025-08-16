"""
Author: Nathaniel Ingle
Date: 27-July-2025

A basic utility file to store algorithms.
"""
import numpy as np


# A function to find a simple linear regression.
# Returns m and b where y = mx + b
# See: https://en.wikipedia.org/wiki/Simple_linear_regression
# This implementation is O(n), as it reads over the data 4 times
# (for finding the average x value, average y value, the numerator, and the denominator),
# giving 4n, which simplifies to n and all other operations are constant time.
def simple_linear_regression(xs, ys):
    x_average = np.mean(xs)
    y_average = np.mean(ys)
    n = len(xs)

    # Find m:
    numer = sum((xs[i] - x_average) * (ys[i] - y_average) for i in range(n))
    denom = sum((xs[i] - x_average) ** 2 for i in range(n))
    m = numer / denom

    # Find b:
    b = y_average - m * x_average

    return m, b
