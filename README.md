## ТЕСТОВОЕ ЗАДАНИЕ В КОНТУР. ПРОЕКТ ОБРАБАТЫВАЛ ФАЙЛЫ КОТОРЫЕ ПОМЕЩАЛИСЬ В ПАМЯТЬ, НО ВОТ ФАЙЛЫ КОТОРЫЕ БОЛЬШЕ(МОГУТ ДОСТИГАТЬ 2,8 ГБ)ОБРАБОТАТЬ НЕ МОЖЕТ,
**********************************************************************************
Chartographer
От charta (лат.) или харта — одно из названий папируса.

Необходимо написать сервис Chartographer — сервис для восстановления изображений древних свитков и папирусов. Изображения растровые и создаются поэтапно (отдельными фрагментами). Восстановленное изображение можно получать фрагментами (даже если оно лишь частично восстановленное).

Предполагается, что этим сервисом будет одновременно пользоваться множество учёных.

HTTP API
Необходимо реализовать 4 HTTP-метода:

### POST /chartas/?width={width}&height={height}
Создать новое изображение папируса заданного размера (в пикселях), где {width} и {height} — положительные целые числа, не превосходящие 20 000 и 50 000, соответственно.
Тело запроса пустое.
В теле ответа возвращается {id} — уникальный идентификатор изображения в строковом представлении.
Код ответа: 201 Created.

### POST /chartas/{id}/?x={x}&y={y}&width={width}&height={height}
Сохранить восстановленный фрагмент изображения размера {width} x {height} с координатами ({x};{y}). Под координатами подразумевается положение левого верхнего угла фрагмента относительно левого верхнего угла всего изображения. Другими словами, левый верхний угол изображения является началом координат, т.е. эта точка имеет координаты (0;0).
Тело запроса: изображение в формате BMP (цвет в RGB, 24 бита на 1 пиксель).
Тело ответа пустое.
Код ответа: 200 OK.

### GET /chartas/{id}/?x={x}&y={y}&width={width}&height={height}
Получить восстановленную часть изображения размера {width} x {height} с координатами ({x};{y}), где {width} и {height} — положительные целые числа, не превосходящие 5 000. Под координатами подразумевается положение левого верхнего угла фрагмента относительно левого верхнего угла всего изображения. Другими словами, левый верхний угол изображения является началом координат, т.е. эта точка имеет координаты (0;0).
Тело ответа: изображение в формате BMP (цвет в RGB, 24 бита на 1 пиксель).
Код ответа: 200 OK.

### DELETE /chartas/{id}/
Удалить изображение с идентификатором {id}.
Тело запроса и ответа пустое.
Код ответа: 200 OK.
