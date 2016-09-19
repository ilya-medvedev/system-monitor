var socket = new WebSocket('ws://' + location.host + '/ws');
socket.binaryType = 'arraybuffer';

Highcharts.setOptions({
    global: {
        useUTC: false
    }
});

$('#cpu').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg,
    },
    title: {
        text: 'CPU'
    },
    xAxis: {
        type: 'datetime',
        maxZoom: 60000
    },
    yAxis: {
        title: {
            text: false
        }
    },
    tooltip: {
        valueSuffix: '%'
    },
    plotOptions: {
        spline: {
            marker: {
                enabled: false
            }
        }
    },
    exporting: {
        enabled: false
    },
    series: [
        {
            id: 'cpu',
            name: 'cpu'
        }
    ]
});

$('#mem').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg,
    },
    title: {
        text: 'Memory'
    },
    xAxis: {
        type: 'datetime',
        maxZoom: 60000
    },
    yAxis: {
        title: {
            text: false
        }
    },
    tooltip: {
        valueSuffix: '%'
    },
    plotOptions: {
        spline: {
            marker: {
                enabled: false
            }
        }
    },
    exporting: {
        enabled: false
    },
    series: [
        {
            id: 'mem',
            name: 'mem'
        }
    ]
});

function bytesPerSecondLabel(bytes) {
    var suffix = ['B/s', 'kB/s', 'MB/s', 'GB/s'];

    var e;
    var value;

    if (bytes == 0) {
        e = 0;
        value = 0;
    } else {
        e = Math.floor(Math.log(bytes) / Math.log(1024));
        value = Math.round((bytes / Math.pow(1024, e)) * 100) / 100;
    };

    return value + ' ' + suffix[e];
}

$('#disk').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg,
    },
    title: {
        text: 'Disk'
    },
    xAxis: {
        type: 'datetime',
        maxZoom: 60000
    },
    yAxis: {
        title: {
            text: false
        },
        labels: {
            formatter: function() {
                return bytesPerSecondLabel(this.value);
            }
        }
    },
    tooltip: {
        valueSuffix: ' B/s'
    },
    plotOptions: {
        spline: {
            marker: {
                enabled: false
            }
        }
    },
    exporting: {
        enabled: false
    },
    series: [
        {
            id: 'read',
            name: 'Read'
        },
        {
            id: 'write',
            name: 'Write'
        }
    ]
});

$('#net').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg,
    },
    title: {
        text: 'Network'
    },
    xAxis: {
        type: 'datetime',
        maxZoom: 60000
    },
    yAxis: {
        title: {
            text: false
        },
        labels: {
            formatter: function() {
                return bytesPerSecondLabel(this.value);
            }
        }
    },
    tooltip: {
        valueSuffix: ' B/s'
    },
    plotOptions: {
        spline: {
            marker: {
                enabled: false
            }
        }
    },
    exporting: {
        enabled: false
    },
    series: [
        {
            id: 'down',
            name: 'Download'
        },
        {
            id: 'up',
            name: 'Upload'
        }
    ]
});

var charts = {
    cpu: $('#cpu').highcharts(),
    mem: $('#mem').highcharts(),
    disk: $('#disk').highcharts(),
    net: $('#net').highcharts()
};

goog.require('proto.SensorMessage');

$(function() {
    socket.onmessage = function(message) {
        var sensorMessage = proto.SensorMessage.deserializeBinary(message.data);

        var value = sensorMessage.getValueList();

        $.each(value, function(index, sensorInfo) {
            var name = sensorInfo.getName();

            var chart = charts[name];

            var value = sensorInfo.getValueList();

            $.each(value, function(index, sensorValue) {
                var name = sensorValue.getName();
                var series = chart.get(name);
                var time = sensorMessage.getTime();
                var value = sensorValue.getValue()
                var point = [time, value];

                if (series == null) {
                    chart.addSeries({
                        id: name,
                        name: name,
                        data: [point],
                        visible: false
                    }, false);
                } else {
                    var shift = series.xData.length > 20;

                    series.addPoint(point, false, shift);
                }
            });

            chart.redraw();
        });
    };
});