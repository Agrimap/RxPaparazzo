/*
 * Copyright 2016 Miguel Garcia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.miguelbcr.ui.rx_paparazzo2;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.miguelbcr.ui.rx_paparazzo2.entities.Config;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.miguelbcr.ui.rx_paparazzo2.entities.Response;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.Size;
import com.miguelbcr.ui.rx_paparazzo2.internal.di.ApplicationComponent;
import com.miguelbcr.ui.rx_paparazzo2.internal.di.ApplicationModule;
import com.yalantis.ucrop.UCrop;

import java.util.List;

import io.reactivex.Observable;
import rx_activity_result2.RxActivityResult;

public final class RxPaparazzo {
  public static final int RESULT_DENIED_PERMISSION = 2;
  public static final int RESULT_DENIED_PERMISSION_NEVER_ASK = 3;

  public static void register(Application application) {
    RxActivityResult.register(application);
  }

  public static <T extends Activity> BuilderImage<T> single(T activity) {
    return new BuilderImage<T>(activity);
  }

  public static <T extends Fragment> BuilderImage<T> single(T fragment) {
    return new BuilderImage<T>(fragment);
  }

  /**
   * Prior to API 18, only one image will be retrieved.
   */
  public static <T extends Activity> BuilderImages<T> multiple(T activity) {
    return new BuilderImages<T>(activity);
  }

  /**
   * Prior to API 18, only one image will be retrieved.
   */
  public static <T extends Fragment> BuilderImages<T> multiple(T fragment) {
    return new BuilderImages<T>(fragment);
  }

  private abstract static class Builder<T> {
    protected final Config config;
    protected final ApplicationComponent applicationComponent;

    public Builder(T ui) {
      this.config = new Config();
      this.applicationComponent = ApplicationComponent.create(new ApplicationModule(config, ui));
    }
  }

  /**
   * Call it when just one image is required to retrieve.
   */
  public static class BuilderImage<T> extends Builder<T> {

    public BuilderImage(T ui) {
      super(ui);
    }

    /**
     * Calling it the images will be saved in internal storage, otherwise in public storage
     */
    public BuilderImage<T> useInternalStorage() {
      this.config.setUseInternalStorage();
      return this;
    }

    /**
     * Sets the size for the retrieved image.
     *
     * @see Size
     */
    public BuilderImage<T> size(Size size) {
      this.config.setSize(size);
      return this;
    }

    /**
     * Sets the mime type of the picker.
     */
    public BuilderImage<T> setMimeType(String mimeType) {
      this.config.setMimeType(mimeType);
      return this;
    }

    /**
     * Call it when crop option is required.
     */
    public BuilderImage<T> crop() {
      this.config.setCrop();
      return this;
    }

    /**
     * Send result to media scanner
     */
    public BuilderImage<T> sendToMediaScanner() {
      this.config.setSendToMediaScanner(true);
      return this;
    }

    /**
     * Send result to media scanner
     */
    public BuilderImage<T> doNotSendToMediaScanner() {
      this.config.setSendToMediaScanner(false);
      return this;
    }

    /**
     * Call it when crop option is required as such as configuring the options of the cropping
     * action.
     */
    public <O extends UCrop.Options> BuilderImage<T> crop(O options) {
      this.config.setCrop(options);
      return this;
    }

    /**
     * Use gallery to retrieve the image.
     */
    public Observable<Response<T, FileData>> usingGallery() {
      return applicationComponent.gallery().pickImage();
    }

    /**
     * Use camera to retrieve the image.
     */
    public Observable<Response<T, FileData>> usingCamera() {
      return applicationComponent.camera().takePhoto();
    }

    /**
     * Use file pickers to retrieve the files.
     */
    public Observable<Response<T, FileData>> usingFiles() {
      return applicationComponent.files().pickFile();
    }
  }

  /**
   * Call it when multiple images are required to retrieve from gallery.
   */
  public static class BuilderImages<T> extends Builder<T> {

    public BuilderImages(T ui) {
      super(ui);
    }

    /**
     * Calling it the images will be saved in internal storage, otherwise in public storage
     */
    public BuilderImages<T> useInternalStorage() {
      this.config.setUseInternalStorage();
      return this;
    }

    /**
     * Sets the size for the retrieved image.
     *
     * @see Size
     */
    public BuilderImages<T> size(Size size) {
      this.config.setSize(size);
      return this;
    }

    /**
     * Call it when crop option is required.
     */
    public BuilderImages<T> crop() {
      this.config.setCrop();
      return this;
    }

    /**
     * Call it when crop option is required as such as configuring the options of the cropping
     * action.
     */
    public <O extends UCrop.Options> BuilderImages<T> crop(O options) {
      this.config.setCrop(options);
      return this;
    }

    /**
     * Call it when crop option is required.
     */
    public Observable<Response<T, List<FileData>>> usingGallery() {
      return applicationComponent.gallery().pickImages();
    }

    /**
     * Use file pickers to retrieve the files.
     */
    public Observable<Response<T, List<FileData>>> usingFiles() {
      return applicationComponent.files().pickFiles();
    }
  }
}
