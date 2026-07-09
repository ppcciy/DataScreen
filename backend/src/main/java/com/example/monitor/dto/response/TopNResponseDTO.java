package com.example.monitor.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TopNResponseDTO {
    private List<TopNResultDTO> cpu;
    private List<TopNResultDTO> memory;
    private List<TopNResultDTO> disk;
    private List<TopNResultDTO> network;
}
