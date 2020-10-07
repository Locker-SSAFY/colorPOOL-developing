package com.ssafy.socks.service.magazine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ssafy.socks.advice.exception.CCommunicationException;
import com.ssafy.socks.advice.exception.CUserNotFoundException;
import com.ssafy.socks.entity.color.ColorHistory;
import com.ssafy.socks.entity.magazine.Bookmark;
import com.ssafy.socks.entity.magazine.Contents;
import com.ssafy.socks.entity.magazine.Likes;
import com.ssafy.socks.entity.magazine.Magazine;
import com.ssafy.socks.entity.user.User;
import com.ssafy.socks.model.magazine.ContentsModel;
import com.ssafy.socks.model.magazine.MagazineModel;
import com.ssafy.socks.repository.color.ColorHistoryJpaRepository;
import com.ssafy.socks.repository.color.SelectedColorJpaRepository;
import com.ssafy.socks.repository.magazine.BookmarkRepository;
import com.ssafy.socks.repository.magazine.ContentsJpaRepository;
import com.ssafy.socks.repository.magazine.LikesJpaRepository;
import com.ssafy.socks.repository.magazine.MagazineJpaRepository;
import com.ssafy.socks.repository.magazine.MagazineRepository;
import com.ssafy.socks.repository.theme.ThemeJpaRepository;
import com.ssafy.socks.repository.user.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MagazineService {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	private final MagazineJpaRepository magazineJpaRepository;
	private final UserJpaRepository userJpaRepository;
	private final MagazineRepository magazineRepository;
	private final LikesJpaRepository likesJpaRepository;
	private final BookmarkRepository bookmarkRepository;
	private final ThemeJpaRepository themeJpaRepository;
	private final ColorHistoryJpaRepository colorHistoryJpaRepository;
	private final SelectedColorJpaRepository selectedColorJpaRepository;
	private final ContentsJpaRepository contentsJpaRepository;

	public void saveMagazine(MagazineModel magazineModel) {
		LocalDateTime currDate = LocalDateTime.now();
		User user = userJpaRepository.findByEmail(magazineModel.getEmail()).orElseThrow(CUserNotFoundException::new);

		Magazine magazine = Magazine.builder()
			.user(user)
			.magazineName(magazineModel.getMagazineName())
			.themeId(magazineModel.getThemeId())
			.selectedId(magazineModel.getSelectedColorId())
			.createdDate(currDate)
			.build();

		logger.info("----------------- magazine -----------------");
		logger.info("user : " + magazine.getUser().getEmail());
		logger.info("themeId : " + magazine.getThemeId());
		logger.info("current Date : " + magazine.getCreatedDate());
		logger.info("----------------- magazine -----------------");

		ColorHistory colorHistory = new ColorHistory();
		colorHistory.setSelectedColor(selectedColorJpaRepository.findById(magazineModel.getSelectedColorId()).orElseThrow(CCommunicationException::new));
		colorHistory.setUser(user);
		colorHistoryJpaRepository.save(colorHistory);

		magazineJpaRepository.save(magazine);

		List<Contents> contentsList = new ArrayList<>();
		for (int i = 0; i < magazineModel.getContents().size(); i++) {
			Contents contents = Contents.builder()
				.url(magazineModel.getContents().get(i).getUrl())
				.answer(magazineModel.getContents().get(i).getAnswer())
				.mainText(magazineModel.getContents().get(i).getMainText())
				.subText(magazineModel.getContents().get(i).getSubText())
				.template(magazineModel.getContents().get(i).getTemplate())
				.question(magazineModel.getContents().get(i).getQuestion())
				.build();
			contentsList.add(contents);
		}

		Magazine findMagazine = magazineJpaRepository.findByMagazineName(magazineModel.getMagazineName()).orElseThrow(CCommunicationException::new);
		for (Contents contents : contentsList) {
			contents.setMagazineId(findMagazine.getId());
			contentsJpaRepository.save(contents);
		}
	}

	public List<MagazineModel> getMagazinesByUser(String userEmail) {
		List<Magazine> magazineList = magazineJpaRepository.findByUser(userJpaRepository.findByEmail(userEmail).orElseThrow(CUserNotFoundException::new));
		List<MagazineModel> magazineModels = new ArrayList<>();

		for(Magazine magazine : magazineList) {
			List<Contents> contentsList = contentsJpaRepository.findByMagazineId(magazine.getId());
			List<ContentsModel> contentsModels = new ArrayList<>();

			for(Contents contents : contentsList) {
				ContentsModel contentsModel = ContentsModel.builder()
					.answer(contents.getAnswer())
					.mainText(contents.getMainText())
					.question(contents.getQuestion())
					.subText(contents.getSubText())
					.template(contents.getTemplate())
					.url(contents.getUrl())
					.build();

				contentsModels.add(contentsModel);
			}

			MagazineModel magazineModel = MagazineModel.builder()
				.email(magazine.getUser().getEmail())
				.magazineName(magazine.getMagazineName())
				.themeId(magazine.getThemeId())
				.selectedColorId(magazine.getSelectedId())
				.userNickname(magazine.getUser().getNickname())
				.contents(contentsModels)
				.build();

			magazineModels.add(magazineModel);
		}

		return magazineModels;
	}

	public List<MagazineModel> getMagazines() {
		List<Magazine> magazineList = magazineJpaRepository.findAll();
		List<MagazineModel> magazineModels = new ArrayList<>();

		for(Magazine magazine : magazineList) {
			List<Contents> contentsList = contentsJpaRepository.findByMagazineId(magazine.getId());
			List<ContentsModel> contentsModels = new ArrayList<>();

			for(Contents contents : contentsList) {
				ContentsModel contentsModel = ContentsModel.builder()
					.answer(contents.getAnswer())
					.mainText(contents.getMainText())
					.question(contents.getQuestion())
					.subText(contents.getSubText())
					.template(contents.getTemplate())
					.url(contents.getUrl())
					.build();

				contentsModels.add(contentsModel);
			}

			MagazineModel magazineModel = MagazineModel.builder()
				.email(magazine.getUser().getEmail())
				.magazineName(magazine.getMagazineName())
				.themeId(magazine.getThemeId())
				.selectedColorId(magazine.getSelectedId())
				.userNickname(magazine.getUser().getNickname())
				.contents(contentsModels)
				.build();

			magazineModels.add(magazineModel);
		}

		return magazineModels;
	}

	public Magazine getMagazine(Long magazineId) {
		return magazineJpaRepository.findById(magazineId).orElseThrow(CCommunicationException::new);
	}

	public void setLikes(Long magazineId, String userEmail) {
		User user = userJpaRepository.findByEmail(userEmail).orElseThrow(CUserNotFoundException::new);
		Magazine magazine = magazineJpaRepository.findById(magazineId).orElseThrow(CCommunicationException::new);
		boolean isSetLike = likesJpaRepository.existsByUserIdAndMagazineId(user.getId(),magazine.getId());
		if(isSetLike) likesJpaRepository.deleteByUserIdAndMagazineId(user.getId(),magazine.getId());
		else likesJpaRepository.save(
			Likes.builder()
				.magazineId(magazine.getId())
				.userId(user.getId())
				.build()
		);
	}

	public List<Magazine> getBookmarkMagazines(String userEmail) {
		User user = userJpaRepository.findByEmail(userEmail).orElseThrow(CUserNotFoundException::new);
		return bookmarkRepository.findBookmarkRepository(user);
	}
}
