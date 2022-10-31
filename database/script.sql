USE [HMS4]
GO
/****** Object:  Table [dbo].[Chore]    Script Date: 2022/10/11 13:43:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Chore](
	[choreID] [int] IDENTITY(1,1) NOT NULL,
	[description] [varchar](255) NOT NULL,
	[isCompleted] [bit] NOT NULL,
	[dateCompleted] [date] NULL,
	[areaName] [varchar](30) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[choreID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[House]    Script Date: 2022/10/11 13:43:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[House](
	[houseName] [varchar](30) NOT NULL,
	[houseCode] [char](4) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[houseName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Housemate]    Script Date: 2022/10/11 13:43:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Housemate](
	[housemateID] [int] IDENTITY(1,1) NOT NULL,
	[username] [varchar](30) NOT NULL,
	[firstName] [varchar](30) NOT NULL,
	[lastName] [varchar](30) NOT NULL,
	[phoneNumber] [char](10) NOT NULL,
	[isLeader] [bit] NOT NULL,
	[password] [varchar](30) NOT NULL,
	[houseName] [varchar](30) NULL,
PRIMARY KEY CLUSTERED 
(
	[housemateID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Resource]    Script Date: 2022/10/11 13:43:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Resource](
	[resourceName] [varchar](30) NOT NULL,
	[isFinished] [bit] NOT NULL,
	[housemateID] [int] NOT NULL,
 CONSTRAINT [PK__Resource__47AE2F62E0EB87E1] PRIMARY KEY CLUSTERED 
(
	[resourceName] ASC,
	[housemateID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Swap]    Script Date: 2022/10/11 13:43:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Swap](
	[housemateID] [int] NOT NULL,
	[choreID] [int] NOT NULL,
	[otherChore] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[housemateID] ASC,
	[choreID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Usage]    Script Date: 2022/10/11 13:43:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Usage](
	[choreID] [int] NOT NULL,
	[resourceName] [varchar](30) NOT NULL,
	[housemateID] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[choreID] ASC,
	[resourceName] ASC,
	[housemateID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Chore] ADD  DEFAULT ((0)) FOR [isCompleted]
GO
ALTER TABLE [dbo].[Housemate] ADD  DEFAULT ((0)) FOR [isLeader]
GO
ALTER TABLE [dbo].[Resource] ADD  DEFAULT ((0)) FOR [isFinished]
GO
ALTER TABLE [dbo].[Housemate]  WITH CHECK ADD  CONSTRAINT [FK__Housemate__house__286302EC] FOREIGN KEY([houseName])
REFERENCES [dbo].[House] ([houseName])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Housemate] CHECK CONSTRAINT [FK__Housemate__house__286302EC]
GO
ALTER TABLE [dbo].[Housemate]  WITH CHECK ADD  CONSTRAINT [FK_Housemate_Housemate] FOREIGN KEY([housemateID])
REFERENCES [dbo].[Housemate] ([housemateID])
GO
ALTER TABLE [dbo].[Housemate] CHECK CONSTRAINT [FK_Housemate_Housemate]
GO
ALTER TABLE [dbo].[Housemate]  WITH CHECK ADD  CONSTRAINT [FK_Housemate_Housemate1] FOREIGN KEY([housemateID])
REFERENCES [dbo].[Housemate] ([housemateID])
GO
ALTER TABLE [dbo].[Housemate] CHECK CONSTRAINT [FK_Housemate_Housemate1]
GO
ALTER TABLE [dbo].[Resource]  WITH CHECK ADD  CONSTRAINT [FK__Resource__housem__2C3393D0] FOREIGN KEY([housemateID])
REFERENCES [dbo].[Housemate] ([housemateID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Resource] CHECK CONSTRAINT [FK__Resource__housem__2C3393D0]
GO
ALTER TABLE [dbo].[Resource]  WITH CHECK ADD  CONSTRAINT [FK_Resource_Resource] FOREIGN KEY([resourceName], [housemateID])
REFERENCES [dbo].[Resource] ([resourceName], [housemateID])
GO
ALTER TABLE [dbo].[Resource] CHECK CONSTRAINT [FK_Resource_Resource]
GO
ALTER TABLE [dbo].[Swap]  WITH CHECK ADD  CONSTRAINT [FK__Swap__choreID__3C69FB99] FOREIGN KEY([choreID])
REFERENCES [dbo].[Chore] ([choreID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Swap] CHECK CONSTRAINT [FK__Swap__choreID__3C69FB99]
GO
ALTER TABLE [dbo].[Swap]  WITH CHECK ADD  CONSTRAINT [FK__Swap__housemateI__3B75D760] FOREIGN KEY([housemateID])
REFERENCES [dbo].[Housemate] ([housemateID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Swap] CHECK CONSTRAINT [FK__Swap__housemateI__3B75D760]
GO
ALTER TABLE [dbo].[Swap]  WITH CHECK ADD FOREIGN KEY([otherChore])
REFERENCES [dbo].[Chore] ([choreID])
GO
ALTER TABLE [dbo].[Swap]  WITH CHECK ADD  CONSTRAINT [FK_Swap_Swap] FOREIGN KEY([housemateID], [choreID])
REFERENCES [dbo].[Swap] ([housemateID], [choreID])
GO
ALTER TABLE [dbo].[Swap] CHECK CONSTRAINT [FK_Swap_Swap]
GO
ALTER TABLE [dbo].[Usage]  WITH CHECK ADD  CONSTRAINT [FK__Usage__73BA3083] FOREIGN KEY([resourceName], [housemateID])
REFERENCES [dbo].[Resource] ([resourceName], [housemateID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Usage] CHECK CONSTRAINT [FK__Usage__73BA3083]
GO
ALTER TABLE [dbo].[Usage]  WITH CHECK ADD  CONSTRAINT [FK__Usage__choreID__72C60C4A] FOREIGN KEY([choreID])
REFERENCES [dbo].[Chore] ([choreID])
ON UPDATE CASCADE
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Usage] CHECK CONSTRAINT [FK__Usage__choreID__72C60C4A]
GO
ALTER TABLE [dbo].[Usage]  WITH CHECK ADD  CONSTRAINT [FK_Usage_Usage] FOREIGN KEY([choreID], [resourceName], [housemateID])
REFERENCES [dbo].[Usage] ([choreID], [resourceName], [housemateID])
GO
ALTER TABLE [dbo].[Usage] CHECK CONSTRAINT [FK_Usage_Usage]
GO
ALTER TABLE [dbo].[Usage]  WITH CHECK ADD  CONSTRAINT [FK_Usage_Usage1] FOREIGN KEY([choreID], [resourceName], [housemateID])
REFERENCES [dbo].[Usage] ([choreID], [resourceName], [housemateID])
GO
ALTER TABLE [dbo].[Usage] CHECK CONSTRAINT [FK_Usage_Usage1]
GO
ALTER TABLE [dbo].[Usage]  WITH CHECK ADD  CONSTRAINT [FK_Usage_Usage2] FOREIGN KEY([choreID], [resourceName], [housemateID])
REFERENCES [dbo].[Usage] ([choreID], [resourceName], [housemateID])
GO
ALTER TABLE [dbo].[Usage] CHECK CONSTRAINT [FK_Usage_Usage2]
GO
