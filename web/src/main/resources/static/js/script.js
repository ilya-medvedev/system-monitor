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

var cpuChart = $('#cpu').highcharts();
var memChart = $('#mem').highcharts();

var SensorValue = dcodeIO.ProtoBuf
        .loadProtoFile("proto/protobuf.proto")
        .build("medvedev.ilya.monitor.proto.SensorValue");

$(function() {
    setInterval(function () {
        cpuChart.redraw();
        memChart.redraw();
    }, 1000);

    socket.onmessage = function(message) {
        var time = new Date()
                .getTime();

        var data = message.data;
        var sensorValue = SensorValue.decode(data);

        var name = sensorValue.name;

        var chart;
        if (name.startsWith('cpu')) {
            chart = cpuChart;
        } else if (name == 'mem' || name == 'swap') {
            chart = memChart;
        } else {
            return;
        }

        var series = chart.get(name);
        var point = [time, sensorValue.value];

        if (series == null) {
            chart.addSeries({
                id: name,
                name: name,
                data: [point],
                visible: false
            }, false);
        } else {
            var shift = series.xData.length > 60;

            series.addPoint(point, false, shift);
        }
    };
});