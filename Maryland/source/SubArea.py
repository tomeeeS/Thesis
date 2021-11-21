
class SubArea:
    def __init__(self, min_lat, max_lat, min_lon, max_lon):
        self.min_lat = min_lat
        self.max_lat = max_lat
        self.min_lon = min_lon
        self.max_lon = max_lon

    def contains(self, lat, lon):
        return (self.min_lon <= lon) & \
            (lon <= self.max_lon) & \
            (self.min_lat <= lat) & \
            (lat <= self.max_lat)
