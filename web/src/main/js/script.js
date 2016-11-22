$('#cpu').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg
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
        valueSuffix: '%',
        shared: true
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
        animation: Highcharts.svg
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
        valueSuffix: '%',
        shared: true
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

function pointFormatter(point) {
    var series = point.series;

    var mark = '<font color="' + series.color + '">●</font>';
    var name = ' ' + series.name + ': ';
    var value = '<b>' + bytesPerSecondLabel(point.y) + '</b>';

    return mark + name + value + '<br/>';
}

$('#disk').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg
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
        pointFormatter: function() {
            return pointFormatter(this);
        },
        useHTML: true,
        shared: true
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
        animation: Highcharts.svg
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
        pointFormatter: function() {
            return pointFormatter(this);
        },
        useHTML: true,
        shared: true
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

function onMessage(message) {
    var sensorMessage = proto.SensorMessage.deserializeBinary(message.data);
    var value = sensorMessage.getValueList();

    $.each(value, function(index, sensorInfo) {
        var name = sensorInfo.getName();
        var div = '#' + name;
        var chart = $(div).highcharts();
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
}

$(function() {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    goog.require('proto.SensorMessage');

    var protocol = location.protocol.replace('http', 'ws');
    var url = protocol + '//' + location.host + '/ws';
    var ws = new WebSocket(url);

    ws.binaryType = "arraybuffer";
    ws.onmessage = onMessage;
});
