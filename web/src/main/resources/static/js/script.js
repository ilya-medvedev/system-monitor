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
        events: {
            load: function () {
                cpuChart = this;
            }
        }
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
            id: "cpu",
            name: "cpu"
        }
    ]
});

$('#mem').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg,
        events: {
            load: function () {
                cpuChart = this;
            }
        }
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
            id: "mem",
            name: "mem"
        }
    ]
});

var charts = {
    cpu: $('#cpu').highcharts(),
    mem: $('#mem').highcharts()
};

goog.require('proto.medvedev.ilya.monitor.protobuf.SensorMessage');

$(function() {
    socket.onmessage = function(message) {
        var sensorMessage = proto.medvedev.ilya.monitor.protobuf.SensorMessage.deserializeBinary(message.data);

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