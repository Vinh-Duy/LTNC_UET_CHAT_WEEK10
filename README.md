# Báo cáo Bài tập Tuần 10 - Hệ thống Chat

## Bài 4: Thiết kế CSDL Quan hệ & Chuẩn hóa

### 1. Sơ đồ Thực thể - Mối quan hệ (ERD)
Hệ thống được thiết kế với 3 thực thể chính để lưu trữ dữ liệu thay vì dùng RAM:
- **Users**: Lưu thông tin người tham gia.
- **Rooms**: Lưu thông tin các phòng chat (theo mô hình room-based của Bài 3).
- **Messages**: Lưu nội dung tin nhắn, liên kết giữa User và Room.

**Mối quan hệ:**
- **User (1) - (N) Message**: Một người dùng có thể gửi nhiều tin nhắn.
- **Room (1) - (N) Message**: Một phòng chứa nhiều tin nhắn từ nhiều người dùng.

### 2. Chuẩn hóa CSDL (Chuẩn 3 - 3NF)
Lược đồ được thiết kế đạt chuẩn 3NF để loại bỏ dư thừa dữ liệu:
- **1NF**: Mọi thuộc tính đều là giá trị nguyên tố.
- **2NF**: Các thuộc tính không phải khóa (`username`, `content`, `sent_at`) phụ thuộc hoàn toàn vào khóa chính của bảng đó.
- **3NF**: Không có sự phụ thuộc bắc cầu giữa các thuộc tính không phải khóa. Thông tin User và Room được tách riêng, bảng Messages chỉ giữ ID (Khóa ngoại).

### 3. Tập lệnh SQL (DDL)
Dưới đây là các câu lệnh SQL để khởi tạo hệ thống, có áp dụng các ràng buộc `PRIMARY KEY`, `FOREIGN KEY`, và `DEFAULT` cho thời gian:

```sql
-- Tạo bảng Người dùng
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng Phòng chat
CREATE TABLE Rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng Tin nhắn
CREATE TABLE Messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    room_id INT NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES Rooms(room_id) ON DELETE CASCADE
);

Bài 5

1. Vấn đề khi chạy đồng thời hai CommandServer trên cùng một cổng:
Khi chạy hai chương trình CommandServer cùng lúc, chương trình chạy sau sẽ ném ra ngoại lệ BindException.
Lý do: Mỗi một cổng (port) trên hệ điều hành tại một thời điểm chỉ cho phép một tiến trình (process) duy nhất được phép "ràng buộc" (bind) để lắng nghe. Do tiến trình thứ nhất đã chiếm dụng cổng 5000, tiến trình thứ hai sẽ bị hệ điều hành từ chối quyền truy cập, dẫn đến lỗi BindException.

2. Sự khác biệt về bản chất giao thức gây ra cách xử lý lỗi khác nhau:

TCP (CommandClient báo lỗi): TCP là giao thức hướng kết nối (Connection-oriented). Trước khi gửi dữ liệu, nó bắt buộc phải thực hiện quá trình "Bắt tay 3 bước" (3-way handshake) để thiết lập một đường truyền tin cậy. Nếu CommandServer chưa chạy, quá trình bắt tay thất bại ngay lập tức, và Java ném ra ngoại lệ ConnectException để báo hiệu cho client biết.

UDP (SensorSender không báo lỗi): UDP là giao thức phi kết nối (Connectionless). Bản chất của nó là "bắn và quên" (fire-and-forget). SensorSender chỉ đơn giản là đóng gói dữ liệu và ném ra mạng tới địa chỉ đích mà không cần quan tâm phía bên kia có ai đang lắng nghe cổng 6000 hay không. Do không có cơ chế kiểm tra kết nối hay phản hồi, nó sẽ không bao giờ báo lỗi dù SensorReceiver có đang tắt.